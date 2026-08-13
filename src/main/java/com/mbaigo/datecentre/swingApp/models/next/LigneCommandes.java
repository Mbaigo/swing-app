package com.mbaigo.datecentre.swingApp.models.next;

import com.mbaigo.datecentre.swingApp.models.Commande;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "lignes_commande")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneCommandes {

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
