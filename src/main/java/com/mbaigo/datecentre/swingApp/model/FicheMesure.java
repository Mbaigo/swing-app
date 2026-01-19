package com.mbaigo.datecentre.swingApp.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "fiches_mesure")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FicheMesure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ex: "Robe de Mariée", "Costume Hiver"
    private String nomProjet;

    @Column(nullable = false)
    private LocalDate datePrise;

    // Le mapping magique : JSONB <-> Java Map
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Double> valeurs = new HashMap<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @ToString.Exclude // Important pour éviter boucle infinie avec Lombok
    private Client client;
}
