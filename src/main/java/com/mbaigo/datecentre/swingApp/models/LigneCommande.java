package com.mbaigo.datecentre.swingApp.models;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "lignes_commande")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private ArticleStock article;

    private Double quantite;

    // On stocke le prix unitaire au moment de la commande (historique de prix)
    private BigDecimal prixUnitaireFacture;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    @ToString.Exclude
    private Commande commande;
}
