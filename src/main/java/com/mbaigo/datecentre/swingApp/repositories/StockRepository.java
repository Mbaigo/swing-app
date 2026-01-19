package com.mbaigo.datecentre.swingApp.repositories;

import com.mbaigo.datecentre.swingApp.enums.CategorieArticle;
import com.mbaigo.datecentre.swingApp.model.ArticleStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<ArticleStock, Long> {

    // Pour filtrer par type (ex: Montre-moi tous les tissus)
    List<ArticleStock> findByCategorie(CategorieArticle categorie);

    /**
     * Trouve tous les articles où la quantité est inférieure ou égale au seuil d'alerte.
     * On exclut les articles qui n'ont pas de seuil défini (NULL).
     */
    @Query("SELECT a FROM ArticleStock a WHERE a.seuilAlerte IS NOT NULL AND a.quantiteDisponible <= a.seuilAlerte")
    List<ArticleStock> findArticlesEnAlerte();
}
