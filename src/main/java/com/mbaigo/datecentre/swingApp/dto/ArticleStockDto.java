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

        // On garde le champ ici pour qu'il apparaisse dans le JSON de réponse
        boolean inAlerte
) {
    // --- CONSTRUCTEUR COMPACT ---
    public ArticleStockDto {
        // Logique : On écrase la valeur de 'enAlerte' automatiquement
        // On vérifie aussi que quantite n'est pas null pour éviter le Crash
        if (quantite == null) {
            quantite = 0.0;
        }

        // Le calcul se fait ici.
        // Si seuilAlerte est null (pas de seuil défini), pas d'alerte.
        inAlerte = (seuilAlerte != null && quantite <= seuilAlerte);
    }
}
