package com.mbaigo.datecentre.swingApp.dto;

import com.mbaigo.datecentre.swingApp.enums.Genre;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Modèle de données pour un client")
public record ClientDto(

        @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "ID unique généré par la base")
        Long id,

        @NotBlank
        @Schema(description = "Nom de famille", example = "Dupont")
        String nom,

        @NotBlank
        @Schema(description = "Prénom", example = "Marie")
        String prenom,

        @NotBlank
        @Schema(description = "Numéro de téléphone unique", example = "0612345678")
        String telephone,

        @Schema(description = "Email de contact", example = "marie.dupont@email.com")
        String email,

        @Schema(description = "Genre du client", example = "FEMME")
        Genre genre,

        @Schema(description = "Notes libres sur la morphologie", example = "Épaules larges, préfère les coupes amples")
        String notesMorphologie){}
