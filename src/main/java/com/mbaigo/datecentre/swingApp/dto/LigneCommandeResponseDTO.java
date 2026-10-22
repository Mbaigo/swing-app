package com.mbaigo.datecentre.swingApp.dto;

import java.math.BigDecimal;
import java.util.List;

public record LigneCommandeResponseDTO (
        Long id,
        String nomMaquette,
        List<String> imagesUrl,
        Integer quantite,
        BigDecimal prixConfection,
        BigDecimal sousTotal
) {}