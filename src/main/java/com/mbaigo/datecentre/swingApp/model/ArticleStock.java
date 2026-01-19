package com.mbaigo.datecentre.swingApp.model;

import com.mbaigo.datecentre.swingApp.enums.CategorieArticle;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_articles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom; // Ex: "Soie Sauvage Rouge"

    private String reference; // SKU ou Ref Fournisseur

    @Enumerated(EnumType.STRING)
    private CategorieArticle categorie;

    // Double permet de gérer 1.5 mètre ou 10 boutons
    @Column(nullable = false)
    private Double quantiteDisponible;

    // Prix d'achat au mètre/unité (BigDecimal pour l'argent !)
    private BigDecimal prixAchatUnitaire;

    // Seuil pour alerte "Stock bas"
    private Double seuilAlerte;

    private String fournisseur;

    @Column(updatable = false)
    private LocalDateTime dateAjout;

    @PrePersist
    protected void onCreate() {
        this.dateAjout = LocalDateTime.now();
    }
}
