package com.mbaigo.datecentre.swingApp.services.impl;

import com.mbaigo.datecentre.swingApp.dto.CommandeRequestDto;
import com.mbaigo.datecentre.swingApp.models.Client;
import com.mbaigo.datecentre.swingApp.repositories.ClientRepository;
import com.mbaigo.datecentre.swingApp.repositories.FicheMesureRepository;
import com.mbaigo.datecentre.swingApp.repositories.StockRepository;
import com.mbaigo.datecentre.swingApp.services.StockService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommandeServiceImpl {

    private final CommandeRepository commandeRepository;
    private final ClientRepository clientRepository;
    private final ModeleRepository modeleRepository;
    private final FicheMesureRepository ficheMesureRepository;
    private final StockRepository stockRepository;
    private final StockService stockService; // Pour utiliser le débit sécurisé

    @Transactional
    public Long createCommande(CommandeRequestDto dto) {
        // 1. Chargement des entités principales
        Client client = clientRepository.findById(dto.clientId())
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable"));

        Modele modele = modeleRepository.findById(dto.modeleId())
                .orElseThrow(() -> new EntityNotFoundException("Modèle introuvable"));

        FicheMesure fiche = null;
        if (dto.ficheMesureId() != null) {
            fiche = ficheMesureRepository.findById(dto.ficheMesureId())
                    .orElseThrow(() -> new EntityNotFoundException("Fiche mesure introuvable"));
        }

        // 2. Initialisation de la commande
        Commande commande = Commande.builder()
                .reference("CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .client(client)
                .modele(modele)
                .ficheMesure(fiche)
                .dateCommande(LocalDate.now())
                .dateLivraisonPrevue(dto.dateLivraisonPrevue())
                .statut(StatutCommande.EN_ATTENTE)
                .acompteVerse(dto.acompte())
                .build();

        // 3. Calcul du coût matériel et gestion du stock
        BigDecimal coutMaterielTotal = BigDecimal.ZERO;

        // On parcourt la recette du modèle
        for (CompositionModele compo : modele.getComposition()) {

            ArticleStock articleFinal = compo.getArticle();
            Double quantiteFinale = compo.getQuantiteRequise();

            // LOGIQUE DE SUBSTITUTION (Si le client veut un autre tissu)
            if (dto.substitutionMateriaux() != null && dto.substitutionMateriaux().containsKey(articleFinal.getId())) {
                Long nouvelArticleId = dto.substitutionMateriaux().get(articleFinal.getId());
                articleFinal = stockRepository.findById(nouvelArticleId)
                        .orElseThrow(() -> new EntityNotFoundException("Article de remplacement introuvable (ID: " + nouvelArticleId + ")"));
            }

            // A. Débit du stock (Lève une exception si stock insuffisant)
            stockService.debiterStock(articleFinal.getId(), quantiteFinale);

            // B. Calcul du coût de cette ligne (Prix achat * Quantité)
            BigDecimal coutLigne = articleFinal.getPrixAchatUnitaire()
                    .multiply(BigDecimal.valueOf(quantiteFinale));

            coutMaterielTotal = coutMaterielTotal.add(coutLigne);

            // C. Ajout de la ligne de consommation à la commande
            LigneCommande ligne = LigneCommande.builder()
                    .article(articleFinal)
                    .quantite(quantiteFinale)
                    .prixUnitaireFacture(articleFinal.getPrixAchatUnitaire())
                    .build();

            commande.addLigne(ligne);
        }

        // 4. Calcul du Prix Total (Main d'œuvre du modèle + Matériel consommé)
        // Optionnel : Tu peux ajouter une marge ici (ex: * 1.5)
        BigDecimal prixTotal = modele.getCoutMainDoeuvre().add(coutMaterielTotal);
        commande.setPrixTotal(prixTotal);

        // 5. Sauvegarde
        return commandeRepository.save(commande).getId();
    }
}
