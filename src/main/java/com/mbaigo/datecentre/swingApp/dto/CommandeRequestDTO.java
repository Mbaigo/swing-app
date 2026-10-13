package com.mbaigo.datecentre.swingApp.dto;

import com.mbaigo.datecentre.swingApp.enums.StatutCommande;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CommandeRequestDTO(
        @NotNull(message = "Le client est obligatoire")
        Long clientId,
        LocalDate dateLivraison,
        List<LigneCommandeRequestDTO> lignes,
        StatutCommande statut

) {}
