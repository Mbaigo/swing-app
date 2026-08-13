package com.mbaigo.datecentre.swingApp.controllers;

import com.mbaigo.datecentre.swingApp.dto.FicheMesureRequestDTO;
import com.mbaigo.datecentre.swingApp.dto.FicheMesureResponseDTO;
import com.mbaigo.datecentre.swingApp.services.FicheMesureService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fiches-mesures")
@RequiredArgsConstructor
@Tag(name = "Mesures", description = "Gestion des fiches de mensurations (JSONB)")
@CrossOrigin(origins = "http://localhost:5173")
public class FicheMesureController {
    private final FicheMesureService ficheMesureService;

    // --- US : Créer une fiche ---
    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER','TAILOR')")
    public ResponseEntity<FicheMesureResponseDTO> createFicheMesure(@Valid @RequestBody FicheMesureRequestDTO requestDTO) {
        FicheMesureResponseDTO createdFiche = ficheMesureService.createFicheMesure(requestDTO);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdFiche.id())
                .toUri();

        return ResponseEntity.created(location).body(createdFiche);
    }

    // --- US : Récupérer une fiche précise ---
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    public ResponseEntity<FicheMesureResponseDTO> getFicheById(@PathVariable Long id) {
        return ResponseEntity.ok(ficheMesureService.getFicheById(id));
    }

    // --- US : Consulter l'historique d'un client (Le plus récent en premier) ---
    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    public ResponseEntity<List<FicheMesureResponseDTO>> getFichesByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(ficheMesureService.getFichesByClientId(clientId));
    }

    // --- US : Mettre à jour une fiche ---
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    public ResponseEntity<FicheMesureResponseDTO> updateFicheMesure(
            @PathVariable Long id,
            @Valid @RequestBody FicheMesureRequestDTO requestDTO) {
        return ResponseEntity.ok(ficheMesureService.updateFicheMesure(id, requestDTO));
    }

    // --- US : Récupérer TOUTES les fiches de mesures (Paginé) ---
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    public ResponseEntity<Page<FicheMesureResponseDTO>> getAllFiches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ficheMesureService.getAllFiches(page, size));
    }
}
