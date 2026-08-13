package com.mbaigo.datecentre.swingApp.dto;

import com.mbaigo.datecentre.swingApp.enums.StatutCommande;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CommandeResponseDTO(
        Long id,
        String reference,
        LocalDateTime dateCommande,
        LocalDate dateLivraison,
        BigDecimal coutTotal,
        ClientResponseDTO client,
        StatutCommande statut,
        List<LigneCommandeResponseDTO> lignes
) {}
