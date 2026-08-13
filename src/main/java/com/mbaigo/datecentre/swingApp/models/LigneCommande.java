package com.mbaigo.datecentre.swingApp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneCommande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;

    @Column(name = "nom_maquette", nullable = false, length = 150)
    private String nomMaquette;

    // URL ou chemin vers l'image uploadée / photo de la maquette
    // ✅ CORRECT (Utiliser l'entité ImageMaquette)
    @OneToMany(mappedBy = "ligneCommande", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ImageMaquette> images = new ArrayList<>();

    public void addImage(ImageMaquette image) {
        images.add(image);
        image.setLigneCommande(this);
    }

    @Column(nullable = false)
    private Integer quantite;

    @Column(name = "prix_confection", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixConfection;

    @Column(name = "sous_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal sousTotal;

    @PrePersist
    @PreUpdate
    public void calculerSousTotal() {
        if (this.prixConfection != null && this.quantite != null) {
            this.sousTotal = this.prixConfection.multiply(BigDecimal.valueOf(this.quantite));
        }
    }
}
