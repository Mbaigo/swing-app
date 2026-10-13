package com.mbaigo.datecentre.swingApp.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardResponseDTO(
        long livraisonsDuJour,
        long commandesEnCours,
        BigDecimal chiffreSemaine,

        // Pour le Doughnut Chart (Ex: {"CREEE": 5, "EN_CONFECTION": 12, ...})
        Map<String, Long> repartitionStatut,

        // Pour le Line Chart comparatif
        List<CommandeResponseDTO> commandesMoisActuel,
        List<CommandeResponseDTO> commandesMoisPrecedent
) {}
