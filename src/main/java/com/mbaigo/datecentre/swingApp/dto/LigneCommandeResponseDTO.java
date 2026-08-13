package com.mbaigo.datecentre.swingApp.dto;

import java.math.BigDecimal;

public record LigneCommandeResponseDTO (
        Long id,
        String nomMaquette,
        String imageUrlMaquette,
        Integer quantite,
        BigDecimal prixConfection,
        BigDecimal sousTotal
) {}