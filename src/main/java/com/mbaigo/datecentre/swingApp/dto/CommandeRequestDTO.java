package com.mbaigo.datecentre.swingApp.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CommandeRequestDTO(
        @NotNull(message = "Le client est obligatoire")
        Long clientId,
        LocalDate dateLivraison,
        List<LigneCommandeRequestDTO> lignes

) {}
