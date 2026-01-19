package com.mbaigo.datecentre.swingApp.repositories;

import com.mbaigo.datecentre.swingApp.enums.CategorieArticle;
import com.mbaigo.datecentre.swingApp.model.ArticleStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<ArticleStock, Long> {

    // Pour filtrer par type (ex: Montre-moi tous les tissus)
    List<ArticleStock> findByCategorie(CategorieArticle categorie);

    // Pour l'alerte stock bas (Feature avancée pour plus tard)
    // SELECT * FROM stock WHERE quantite < seuil
    // List<ArticleStock> findByQuantiteDisponibleLessThan(Double seuil);
}
