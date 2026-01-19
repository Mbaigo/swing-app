package com.mbaigo.datecentre.swingApp.services.impl;

import com.mbaigo.datecentre.swingApp.dto.ArticleStockDto;
import com.mbaigo.datecentre.swingApp.model.ArticleStock;
import com.mbaigo.datecentre.swingApp.repositories.StockRepository;
import com.mbaigo.datecentre.swingApp.services.StockService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Override
    public List<ArticleStockDto> getAllArticles() {
        return stockRepository.findAll().stream().map(this::mapToDto).toList();
    }

    @Override
    @Transactional
    public Long ajouterArticle(ArticleStockDto dto) {
        ArticleStock article = ArticleStock.builder()
                .nom(dto.nom())
                .reference(dto.reference())
                .categorie(dto.categorie())
                .quantiteDisponible(dto.quantite())
                .prixAchatUnitaire(dto.prixAchat())
                .seuilAlerte(dto.seuilAlerte())
                .fournisseur(dto.fournisseur())
                .build();
        return stockRepository.save(article).getId();
    }

    @Override
    @Transactional
    public ArticleStockDto updateArticle(Long id, ArticleStockDto dto) {
        ArticleStock article = stockRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article introuvable"));

        article.setNom(dto.nom());
        article.setReference(dto.reference());
        article.setPrixAchatUnitaire(dto.prixAchat());
        article.setFournisseur(dto.fournisseur());
        // On ne modifie pas la quantité ici directement, on passe par créditer/débiter
        // ou une méthode d'inventaire spécifique, mais pour l'admin on peut laisser :
        article.setQuantiteDisponible(dto.quantite());

        return mapToDto(stockRepository.save(article));
    }

    @Override
    @Transactional // CRITIQUE : Empêche les accès concurrents corrompus
    public void debiterStock(Long id, Double quantiteNecessaire) {
        ArticleStock article = stockRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article introuvable"));

        if (article.getQuantiteDisponible() < quantiteNecessaire) {
            throw new IllegalArgumentException(
                    "Stock insuffisant pour " + article.getNom() +
                            ". Demandé: " + quantiteNecessaire +
                            ", Disponible: " + article.getQuantiteDisponible());
        }

        article.setQuantiteDisponible(article.getQuantiteDisponible() - quantiteNecessaire);
        stockRepository.save(article);
    }

    @Override
    @Transactional
    public void crediterStock(Long id, Double quantiteAjoutee) {
        ArticleStock article = stockRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article introuvable"));

        article.setQuantiteDisponible(article.getQuantiteDisponible() + quantiteAjoutee);
        stockRepository.save(article);
    }

    private ArticleStockDto mapToDto(ArticleStock a) {
        return new ArticleStockDto(
                a.getId(), a.getNom(), a.getReference(), a.getCategorie(),
                a.getQuantiteDisponible(), a.getPrixAchatUnitaire(),
                a.getSeuilAlerte(), a.getFournisseur()
        );
    }
}
