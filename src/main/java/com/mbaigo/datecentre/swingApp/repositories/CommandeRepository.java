package com.mbaigo.datecentre.swingApp.repositories;

import com.mbaigo.datecentre.swingApp.enums.StatutCommande;
import com.mbaigo.datecentre.swingApp.models.Client;
import com.mbaigo.datecentre.swingApp.models.Commande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CommandeRepository extends JpaRepository <Commande, Long> {
    // Trouve toutes les commandes créées entre deux instants (idéal pour les jours et les semaines)
    @EntityGraph(attributePaths = {"client"})
    Page<Commande> findByDateCommandeBetween(LocalDateTime debut, LocalDateTime fin, Pageable pageable);

    // Pour trouver les commandes à livrer un jour précis
    @EntityGraph(attributePaths = {"client"})
    Page<Commande> findByDateLivraison(LocalDate date, Pageable pageable);

    // Pour trouver les commandes à livrer sur un intervalle (ex: une semaine)
    @EntityGraph(attributePaths = {"client"})
    Page<Commande> findByDateLivraisonBetween(LocalDate debut, LocalDate fin, Pageable pageable);
    // 1. Recherche par statut exact
    @EntityGraph(attributePaths = {"client"})
    Page<Commande> findByStatut(StatutCommande statut, Pageable pageable);

    // 1. Compter les commandes avec un statut spécifique (ex: EN_CONFECTION ou ESSAYAGE)
    @Query("SELECT COUNT(c) FROM Commande c WHERE c.statut IN :statuts")
    long countByStatutIn(@Param("statuts") List<StatutCommande> statuts);

    // 2. Calculer la somme du CA sur une période
    @Query("SELECT COALESCE(SUM(c.coutTotal), 0) FROM Commande c WHERE c.dateCommande BETWEEN :debut AND :fin")
    BigDecimal sumChiffreAffairesBetween(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    // 3. Regrouper le nombre de commandes par statut (Pour le Doughnut)
// On utilise Object[] car Hibernate renvoie un tableau contenant [Statut, Compte]
    @Query("SELECT c.statut, COUNT(c) FROM Commande c WHERE c.dateCommande BETWEEN :debut AND :fin GROUP BY c.statut")
    List<Object[]> countCommandesByStatutGrouped(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
}
