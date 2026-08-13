package com.mbaigo.datecentre.swingApp.services;

import com.mbaigo.datecentre.swingApp.dto.CommandeRequestDTO;
import com.mbaigo.datecentre.swingApp.dto.CommandeResponseDTO;
import com.mbaigo.datecentre.swingApp.enums.StatutCommande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface CommandeService {
    // --- CRUD DE BASE ---
    CommandeResponseDTO creerCommande(CommandeRequestDTO request);

    CommandeResponseDTO getCommandeById(Long id);

    // On met à jour généralement juste le statut ou la date de livraison pour une commande
    CommandeResponseDTO mettreAJourStatut(Long id, StatutCommande nouveauStatut);

    void supprimerCommande(Long id);

    Page<CommandeResponseDTO> getAllCommandes(Pageable pageable);

    // --- RAPPORTS TEMPORELS ---

    // Liste des commandes pour un jour précis (ex: LocalDate.now())
    Page<CommandeResponseDTO> getCommandesParJour(LocalDate date, Pageable pageable);

    // Liste des commandes pour la semaine correspondant à la date fournie
    Page<CommandeResponseDTO> getCommandesParSemaine(LocalDate dateDansLaSemaine, Pageable pageable);

    // --- LIVRAISONS ---

    // Liste des commandes dont la date de livraison est aujourd'hui
    Page<CommandeResponseDTO> getCommandesALivrerAujourdhui(Pageable pageable);

    // Liste des commandes dont la date de livraison tombe dans la semaine en cours (Lundi -> Dimanche)
    Page<CommandeResponseDTO> getCommandesALivrerCetteSemaine(Pageable pageable);
}
