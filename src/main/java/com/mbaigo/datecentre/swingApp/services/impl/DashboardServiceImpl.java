package com.mbaigo.datecentre.swingApp.services.impl;


import com.mbaigo.datecentre.swingApp.dto.CommandeResponseDTO;
import com.mbaigo.datecentre.swingApp.dto.DashboardResponseDTO;
import com.mbaigo.datecentre.swingApp.dto.mappers.CommandeMapper;
import com.mbaigo.datecentre.swingApp.enums.StatutCommande;
import com.mbaigo.datecentre.swingApp.repositories.CommandeRepository;
import com.mbaigo.datecentre.swingApp.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final CommandeRepository commandeRepository;
    private final CommandeMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public DashboardResponseDTO getDashboardData() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        // 1. Livraisons du jour
        long livraisonsDuJour = commandeRepository.findByDateLivraison(today, Pageable.unpaged()).getTotalElements();

        // 2. Commandes en cours (En confection + Essayage)
        long commandesEnCours = commandeRepository.countByStatutIn(List.of(StatutCommande.EN_CONFECTION, StatutCommande.ESSAYAGE));

        // 3. Chiffre d'affaires de la semaine
        LocalDate lundi = today.with(DayOfWeek.MONDAY);
        LocalDate dimanche = today.with(DayOfWeek.SUNDAY);
        BigDecimal chiffreSemaine = commandeRepository.sumChiffreAffairesBetween(lundi.atStartOfDay(), dimanche.atTime(LocalTime.MAX));

        // 4. Répartition des statuts de la SEMAINE (Pour le Doughnut chart)
        List<Object[]> repartitionRaw = commandeRepository.countCommandesByStatutGrouped(lundi.atStartOfDay(), dimanche.atTime(LocalTime.MAX));
        Map<String, Long> repartition = new HashMap<>();

        // Initialisation à zéro pour garantir que les 4 couleurs du Doughnut ont une valeur
        repartition.put(StatutCommande.CREEE.name(), 0L);
        repartition.put(StatutCommande.EN_CONFECTION.name(), 0L);
        repartition.put(StatutCommande.ESSAYAGE.name(), 0L);
        repartition.put(StatutCommande.TERMINEE.name(), 0L);

        for (Object[] row : repartitionRaw) {
            StatutCommande statut = (StatutCommande) row[0];
            Long count = (Long) row[1];
            repartition.put(statut.name(), count);
        }

        // 5. Comparaison Mois N vs Mois N-1
        YearMonth currentMonth = YearMonth.now();
        YearMonth previousMonth = currentMonth.minusMonths(1);

        List<CommandeResponseDTO> moisActuel = commandeRepository.findByDateCommandeBetween(
                currentMonth.atDay(1).atStartOfDay(),
                currentMonth.atEndOfMonth().atTime(LocalTime.MAX),
                Pageable.unpaged()
        ).stream().map(mapper::toResponse).collect(Collectors.toList());

        List<CommandeResponseDTO> moisPrecedent = commandeRepository.findByDateCommandeBetween(
                previousMonth.atDay(1).atStartOfDay(),
                previousMonth.atEndOfMonth().atTime(LocalTime.MAX),
                Pageable.unpaged()
        ).stream().map(mapper::toResponse).collect(Collectors.toList());

        // 6. Assemblage final
        return new DashboardResponseDTO(
                livraisonsDuJour,
                commandesEnCours,
                chiffreSemaine,
                repartition,
                moisActuel,
                moisPrecedent
        );
    }
}
