package com.mbaigo.datecentre.swingApp.controllers;

import com.mbaigo.datecentre.swingApp.dto.FicheMesureDto;
import com.mbaigo.datecentre.swingApp.services.FicheMesureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fiches-mesures")
@RequiredArgsConstructor
@Tag(name = "Mesures", description = "Gestion des fiches de mensurations (JSONB)")
@CrossOrigin(origins = "http://localhost:5173")
public class FicheMesureController {

    private final FicheMesureService ficheService;

    @Operation(summary = "Ajouter une fiche de mesures")
    @PostMapping
    public ResponseEntity<Long> createFiche(@RequestBody @Valid FicheMesureDto dto) {
        Long id = ficheService.createFiche(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @Operation(summary = "Récupérer l'historique d'un client")
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<FicheMesureDto>> getFichesByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(ficheService.getFichesByClient(clientId));
    }

    @Operation(summary = "Détail d'une fiche")
    @GetMapping("/{id}")
    public ResponseEntity<FicheMesureDto> getFicheById(@PathVariable Long id) {
        return ResponseEntity.ok(ficheService.getFicheById(id));
    }

    // ... imports existants ...

    @Operation(summary = "Modifier une fiche existante", description = "Met à jour les mesures (JSON) ou le nom du projet.")
    @ApiResponse(responseCode = "200", description = "Fiche mise à jour")
    @PutMapping("/{id}")
    public ResponseEntity<FicheMesureDto> updateFiche(
            @PathVariable Long id,
            @RequestBody @Valid FicheMesureDto dto) {

        FicheMesureDto updated = ficheService.updateFiche(id, dto);
        return ResponseEntity.ok(updated);
    }
}
