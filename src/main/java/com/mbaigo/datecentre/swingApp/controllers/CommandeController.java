package com.mbaigo.datecentre.swingApp.controllers;

import com.mbaigo.datecentre.swingApp.dto.CommandeRequestDto;
import com.mbaigo.datecentre.swingApp.services.impl.CommandeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/commandes")
@RequiredArgsConstructor
@Tag(name = "Commandes", description = "Gestion de la production basée sur les modèles")
@CrossOrigin(origins = "http://localhost:5173")
public class CommandeController {

    private final CommandeServiceImpl commandeService;

    @Operation(summary = "Créer une commande via Modèle",
            description = "Sélectionne un modèle, applique les éventuelles substitutions de tissu, calcule le prix final (Main d'œuvre + Matériel) et débite le stock.")
    @PostMapping
    public ResponseEntity<Long> createCommande(@RequestBody @Valid CommandeRequestDto dto) {
        Long id = commandeService.createCommande(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }
}
