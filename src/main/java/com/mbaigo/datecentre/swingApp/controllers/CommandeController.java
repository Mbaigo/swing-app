package com.mbaigo.datecentre.swingApp.controllers;

import com.mbaigo.datecentre.swingApp.dto.CommandeRequestDTO;
import com.mbaigo.datecentre.swingApp.dto.CommandeResponseDTO;
import com.mbaigo.datecentre.swingApp.enums.StatutCommande;
import com.mbaigo.datecentre.swingApp.services.CommandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/commandes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // À adapter selon le port de ton frontend Vue.js (ex: "http://localhost:5173")
public class CommandeController {

    private final CommandeService commandeService;

    // ==========================================
    // CRUD DE BASE
    // ==========================================

    @PostMapping
    public ResponseEntity<CommandeResponseDTO> creerCommande(@Valid @RequestBody CommandeRequestDTO request) {
        CommandeResponseDTO response = commandeService.creerCommande(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeResponseDTO> getCommandeById(@PathVariable Long id) {
        return ResponseEntity.ok(commandeService.getCommandeById(id));
    }

    @GetMapping
    public ResponseEntity<Page<CommandeResponseDTO>> getAllCommandes(Pageable pageable) {
        return ResponseEntity.ok(commandeService.getAllCommandes(pageable));
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<CommandeResponseDTO> mettreAJourStatut(
            @PathVariable Long id,
            @RequestParam StatutCommande statut) {
        return ResponseEntity.ok(commandeService.mettreAJourStatut(id, statut));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerCommande(@PathVariable Long id) {
        commandeService.supprimerCommande(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // FILTRES PAR DATE DE CRÉATION
    // ==========================================

    @GetMapping("/recherche/jour")
    public ResponseEntity<Page<CommandeResponseDTO>> getCommandesParJour(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Pageable pageable) {
        return ResponseEntity.ok(commandeService.getCommandesParJour(date, pageable));
    }

    @GetMapping("/recherche/semaine")
    public ResponseEntity<Page<CommandeResponseDTO>> getCommandesParSemaine(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Pageable pageable) {
        return ResponseEntity.ok(commandeService.getCommandesParSemaine(date, pageable));
    }

    // ==========================================
    // GESTION DES LIVRAISONS (TABLEAU DE BORD)
    // ==========================================

    @GetMapping("/livraisons/aujourdhui")
    public ResponseEntity<Page<CommandeResponseDTO>> getCommandesALivrerAujourdhui(Pageable pageable) {
        return ResponseEntity.ok(commandeService.getCommandesALivrerAujourdhui(pageable));
    }

    @GetMapping("/livraisons/semaine")
    public ResponseEntity<Page<CommandeResponseDTO>> getCommandesALivrerCetteSemaine(Pageable pageable) {
        return ResponseEntity.ok(commandeService.getCommandesALivrerCetteSemaine(pageable));
    }
}