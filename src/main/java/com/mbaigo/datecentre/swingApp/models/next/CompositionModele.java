package com.mbaigo.datecentre.swingApp.models.next;

import com.mbaigo.datecentre.swingApp.models.Modele;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "composition_modeles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompositionModele {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modele_id")
    @ToString.Exclude
    private Modele modele;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "article_stock_id")
    private ArticleStock article;

    @Column(nullable = false)
    private Double quantiteRequise; // Ex: 3.5m
}
