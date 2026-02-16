package com.mbaigo.datecentre.swingApp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record CommandeRequestDto(
        @NotNull Long clientId,
        @NotNull Long modeleId,
        Long ficheMesureId,
        LocalDate dateLivraisonPrevue,
        @Positive BigDecimal acompte,

        // Optionnel : Permet de remplacer un matériau par un autre.
        // Clé = ID de l'article du modèle (celui qu'on enlève)
        // Valeur = ID du nouvel article (celui qu'on met à la place)
        // Ex: { "5": "12" } -> "Remplace l'article ID 5 (Soie Rouge) par l'article ID 12 (Soie Bleue)"
        Map<Long, Long> substitutionMateriaux
) {}
