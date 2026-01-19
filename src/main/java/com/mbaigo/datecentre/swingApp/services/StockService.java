package com.mbaigo.datecentre.swingApp.services;

import com.mbaigo.datecentre.swingApp.dto.ArticleStockDto;

import java.util.List;

public interface StockService {
    List<ArticleStockDto> getAllArticles();
    Long ajouterArticle(ArticleStockDto dto);
    ArticleStockDto updateArticle(Long id, ArticleStockDto dto);

    /**
     * Tente de retirer une quantité du stock.
     * @param id Article concerné
     * @param quantiteNecessaire Quantité à retirer
     * @throws IllegalArgumentException si stock insuffisant
     */
    void debiterStock(Long id, Double quantiteNecessaire);

    // Pour rajouter du stock (réassort)
    void crediterStock(Long id, Double quantiteAjoutee);
}
