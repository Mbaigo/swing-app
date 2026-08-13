package com.mbaigo.datecentre.swingApp.dto;

import com.mbaigo.datecentre.swingApp.enums.Genre;

import java.time.LocalDateTime;

public record ClientResponseDTO(
        Long id,
        String nom,
        String prenom,
        String telephone,
        String email,
        Genre genre,
        String notesMorphologie,
        LocalDateTime dateCreation
) {}
