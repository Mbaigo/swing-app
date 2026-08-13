package com.mbaigo.datecentre.swingApp.models;
import com.mbaigo.datecentre.swingApp.enums.StatutCommande;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commandes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String reference; // ex: CMD-2026-001

    @Column(name = "date_commande", nullable = false)
    private LocalDateTime dateCommande;

    @Column(name = "date_livraison", nullable = false)
    private LocalDate dateLivraison;

    @Column(name = "cout_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal coutTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCommande statut; // EN_ATTENTE, EN_COURS, PRETE, LIVREE, ANNULEE

    // Dans Commande.java

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LigneCommande> lignes = new ArrayList<>();

    public void calculerCoutTotal() {
        this.coutTotal = lignes.stream()
                .map(LigneCommande::getSousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addLigne(LigneCommande ligne) {
        lignes.add(ligne);
        ligne.setCommande(this);
    }
}
