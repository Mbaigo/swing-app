package com.mbaigo.datecentre.swingApp.repositories;

import com.mbaigo.datecentre.swingApp.enums.StatutCommande;
import com.mbaigo.datecentre.swingApp.models.Client;
import com.mbaigo.datecentre.swingApp.models.Commande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface CommandeRepository extends JpaRepository <Commande, Long> {
    // Trouve toutes les commandes créées entre deux instants (idéal pour les jours et les semaines)
    Page<Commande> findByDateCommandeBetween(LocalDateTime debut, LocalDateTime fin, Pageable pageable);

    // Pour trouver les commandes à livrer un jour précis
    Page<Commande> findByDateLivraison(LocalDate date, Pageable pageable);

    // Pour trouver les commandes à livrer sur un intervalle (ex: une semaine)
    Page<Commande> findByDateLivraisonBetween(LocalDate debut, LocalDate fin, Pageable pageable);
    // 1. Recherche par statut exact
    Page<Commande> findByStatut(StatutCommande statut, Pageable pageable);

    // 2. Recherche par période (Sera utilisée pour les mois ET les périodes personnalisées)
    ///Page<Commande> findByDateCommandeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
