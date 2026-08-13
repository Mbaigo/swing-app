package com.mbaigo.datecentre.swingApp.dto;

import java.time.LocalDate;
import java.util.List;

public record CommandeRequestDTO(
        Long clientId,
        LocalDate dateLivraison,
        List<LigneCommandeRequestDTO> lignes

) {}
