package com.mbaigo.datecentre.swingApp.models;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modeles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Modele {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom; // Ex: "Robe Cocktail Col V"

    @Column(columnDefinition = "TEXT")
    private String description;

    // Coût de la main d'œuvre (Temps couturier * Taux horaire)
    @Column(nullable = false)
    private BigDecimal coutMainDoeuvre;

    // La "Recette" technique (Liste des matériaux requis par défaut)
    @OneToMany(mappedBy = "modele", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CompositionModele> composition = new ArrayList<>();
}
