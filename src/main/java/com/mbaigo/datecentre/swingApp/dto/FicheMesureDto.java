package com.mbaigo.datecentre.swingApp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Map;

public record FicheMesureDto(
        Long id,

        @Schema(description = "Nom du projet ou étiquette (ex: Robe Soirée)", example = "Robe Soirée Rouge")
        String nomProjet,

        LocalDate datePrise,

        @Schema(description = "Paires clé-valeur des mesures en cm", example = "{\"tour_taille\": 85.5, \"longueur_bras\": 60.0}")
        Map<String, Double> valeurs,

        @NotNull(message = "L'ID du client est obligatoire")
        Long clientId
) {}
