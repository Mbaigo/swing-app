package com.mbaigo.datecentre.swingApp.services.impl;

import com.mbaigo.datecentre.swingApp.dto.CommandeRequestDTO;
import com.mbaigo.datecentre.swingApp.dto.CommandeResponseDTO;
import com.mbaigo.datecentre.swingApp.dto.LigneCommandeRequestDTO;
import com.mbaigo.datecentre.swingApp.dto.mappers.CommandeMapper;
import com.mbaigo.datecentre.swingApp.enums.StatutCommande;
import com.mbaigo.datecentre.swingApp.models.*;
import com.mbaigo.datecentre.swingApp.models.LigneCommande;
import com.mbaigo.datecentre.swingApp.repositories.*;
import com.mbaigo.datecentre.swingApp.services.CommandeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository commandeRepository;
    private final CommandeMapper mapper;
    private final ClientRepository clientRepository;

    @Transactional
    @Override
    public CommandeResponseDTO creerCommande(CommandeRequestDTO request) {
        // 1. Récupération du client
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable"));

        // 2. Initialisation de la commande
        Commande commande = new Commande();
        commande.setReference("CMD-" + System.currentTimeMillis());
        commande.setDateCommande(LocalDateTime.now());
        commande.setDateLivraison(request.dateLivraison());
        commande.setStatut(StatutCommande.CREEE);
        commande.setClient(client);

        // 3. Construction des lignes et des images
        for (LigneCommandeRequestDTO ligneDTO : request.lignes()) {
            LigneCommande ligne = LigneCommande.builder()
                    .nomMaquette(ligneDTO.nomMaquette())
                    .quantite(ligneDTO.quantite())
                    .prixConfection(ligneDTO.prixConfection())
                    .build();

            // Ajout des multiples images
            if (ligneDTO.imagesUrl() != null) {
                for (String url : ligneDTO.imagesUrl()) {
                    ligne.addImage(ImageMaquette.builder().url(url).build());
                }
            }

            ligne.calculerSousTotal();
            commande.addLigne(ligne);
        }

        commande.calculerCoutTotal();
        Commande savedCommande = commandeRepository.save(commande);

        return mapper.toResponse(savedCommande);
    }

    @Transactional(readOnly = true)
    @Override
    public CommandeResponseDTO getCommandeById(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commande introuvable avec l'ID: " + id));
        return mapper.toResponse(commande);
    }

    @Transactional
    @Override
    public CommandeResponseDTO mettreAJourStatut(Long id, StatutCommande nouveauStatut) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commande introuvable"));

        commande.setStatut(nouveauStatut);
        // Hibernate détecte le changement et fera l'UPDATE automatiquement
        return mapper.toResponse(commande);
    }

    @Transactional
    @Override
    public void supprimerCommande(Long id) {
        if (!commandeRepository.existsById(id)) {
            throw new EntityNotFoundException("Commande introuvable");
        }
        commandeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CommandeResponseDTO> getAllCommandes(Pageable pageable) {
        return commandeRepository.findAll(pageable)
                .map(mapper::toResponse);
    }

    // --- LOGIQUE TEMPORELLE ---

    @Transactional(readOnly = true)
    @Override
    public Page<CommandeResponseDTO> getCommandesParJour(LocalDate date, Pageable pageable) {
        // Ex: De 2026-06-01T00:00:00 à 2026-06-01T23:59:59.999999999
        LocalDateTime debutJournee = date.atStartOfDay();
        LocalDateTime finJournee = date.atTime(LocalTime.MAX);

        return commandeRepository.findByDateCommandeBetween(debutJournee, finJournee, pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CommandeResponseDTO> getCommandesParSemaine(LocalDate dateDansLaSemaine, Pageable pageable) {
        // 1. On trouve le Lundi de cette semaine-là
        LocalDate lundi = dateDansLaSemaine.with(DayOfWeek.MONDAY);
        // 2. On trouve le Dimanche de cette semaine-là
        LocalDate dimanche = dateDansLaSemaine.with(DayOfWeek.SUNDAY);

        // 3. On convertit en LocalDateTime pour couvrir l'intervalle complet
        LocalDateTime debutSemaine = lundi.atStartOfDay();
        LocalDateTime finSemaine = dimanche.atTime(LocalTime.MAX);

        return commandeRepository.findByDateCommandeBetween(debutSemaine, finSemaine, pageable)
                .map(mapper::toResponse);
    }


    // Dans CommandeServiceImpl.java

    // --- LOGIQUE DES LIVRAISONS ---

    @Transactional(readOnly = true)
    @Override
    public Page<CommandeResponseDTO> getCommandesALivrerAujourdhui(Pageable pageable) {
        // Récupère la date du jour (ex: 2026-08-13)
        LocalDate aujourdhui = LocalDate.now();

        return commandeRepository.findByDateLivraison(aujourdhui, pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CommandeResponseDTO> getCommandesALivrerCetteSemaine(Pageable pageable) {
        LocalDate aujourdhui = LocalDate.now();

        // 1. Détermine le Lundi de la semaine actuelle
        LocalDate lundi = aujourdhui.with(DayOfWeek.MONDAY);

        // 2. Détermine le Dimanche de la semaine actuelle
        LocalDate dimanche = aujourdhui.with(DayOfWeek.SUNDAY);

        // 3. Récupère toutes les livraisons prévues entre ce Lundi et ce Dimanche inclus
        return commandeRepository.findByDateLivraisonBetween(lundi, dimanche, pageable)
                .map(mapper::toResponse);
    }
}
