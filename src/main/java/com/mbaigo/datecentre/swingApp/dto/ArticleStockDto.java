package com.mbaigo.datecentre.swingApp.dto;

import com.mbaigo.datecentre.swingApp.enums.CategorieArticle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record ArticleStockDto(
        Long id,
        @NotBlank String nom,
        String reference,
        @NotNull CategorieArticle categorie,
        @PositiveOrZero Double quantite,
        @PositiveOrZero BigDecimal prixAchat,
        Double seuilAlerte,
        String fournisseur,
        // Nouveau champ calculé (sera true si le stock est bas)
        boolean isSeuilAlerte
) {}
