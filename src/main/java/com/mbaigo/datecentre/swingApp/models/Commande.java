package com.mbaigo.datecentre.swingApp.models;
import com.mbaigo.datecentre.swingApp.enums.StatutCommande;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commandes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String reference;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "modele_id", nullable = false)
    private Modele modele; // Le patron utilisé

    @ManyToOne
    @JoinColumn(name = "fiche_mesure_id")
    private FicheMesure ficheMesure; // Les mesures utilisées

    private LocalDate dateCommande;
    private LocalDate dateLivraisonPrevue;

    @Enumerated(EnumType.STRING)
    private StatutCommande statut;

    // Prix calculé automatiquement (Matériel + Main d'œuvre)
    private BigDecimal prixTotal;
    private BigDecimal acompteVerse;

    // Liste des matériaux *réellement* consommés pour CETTE commande
    // (car on peut changer le tissu par rapport au modèle de base)
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LigneCommande> lignesConsommmation = new ArrayList<>();

    public void addLigne(LigneCommande ligne) {
        lignesConsommmation.add(ligne);
        ligne.setCommande(this);
    }
}
