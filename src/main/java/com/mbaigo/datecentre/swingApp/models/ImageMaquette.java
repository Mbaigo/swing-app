package com.mbaigo.datecentre.swingApp.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "images_maquette")
@Setter @Getter @Builder @AllArgsConstructor @NoArgsConstructor
public class ImageMaquette {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ligne_commande_id", nullable = false)
    private LigneCommande ligneCommande;

    @Column(nullable = false, length = 500)
    private String url; // L'URL Cloudinary ou le chemin local

    // Utile pour afficher "Face", "Dos", etc.
    @Column(length = 50)
    private String description;
}
