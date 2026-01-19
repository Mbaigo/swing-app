package com.mbaigo.datecentre.swingApp.services.impl;

import com.mbaigo.datecentre.swingApp.dto.FicheMesureDto;
import com.mbaigo.datecentre.swingApp.model.Client;
import com.mbaigo.datecentre.swingApp.model.FicheMesure;
import com.mbaigo.datecentre.swingApp.repositories.ClientRepository;
import com.mbaigo.datecentre.swingApp.repositories.FicheMesureRepository;
import com.mbaigo.datecentre.swingApp.services.FicheMesureService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FicheMesureServiceImpl implements FicheMesureService {

    private final FicheMesureRepository ficheRepository;
    private final ClientRepository clientRepository;

    @Override
    @Transactional
    public Long createFiche(FicheMesureDto dto) {
        Client client = clientRepository.findById(dto.clientId())
                .orElseThrow(() -> new EntityNotFoundException("Client non trouvé"));

        FicheMesure fiche = FicheMesure.builder()
                .nomProjet(dto.nomProjet())
                .datePrise(dto.datePrise() != null ? dto.datePrise() : LocalDate.now())
                .valeurs(dto.valeurs()) // Ici, la Map est convertie en JSONB automatiquement
                .client(client)
                .build();

        return ficheRepository.save(fiche).getId();
    }

    @Override
    public List<FicheMesureDto> getFichesByClient(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new EntityNotFoundException("Client non trouvé");
        }

        return ficheRepository.findByClientIdOrderByDatePriseDesc(clientId).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public FicheMesureDto getFicheById(Long id) {
        return ficheRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Fiche de mesure introuvable"));
    }

    @Override
    @Transactional
    public FicheMesureDto updateFiche(Long id, FicheMesureDto dto) {
        // 1. Récupérer la fiche existante
        FicheMesure fiche = ficheRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Impossible de modifier : Fiche introuvable (ID: " + id + ")"));

        // 2. Mise à jour des champs simples
        fiche.setNomProjet(dto.nomProjet());

        // Si la date est fournie on la change, sinon on garde l'ancienne
        if (dto.datePrise() != null) {
            fiche.setDatePrise(dto.datePrise());
        }

        // 3. Mise à jour du JSONB (Le cœur de la modif)
        // On remplace complètement les anciennes mesures par les nouvelles
        if (dto.valeurs() != null) {
            fiche.setValeurs(dto.valeurs());
        }
        // 4. Sauvegarde automatique grâce à @Transactional (Dirty Checking),
        // mais le save explicite est une bonne pratique pour la lisibilité.
        FicheMesure updatedFiche = ficheRepository.save(fiche);

        return mapToDto(updatedFiche);
    }

    private FicheMesureDto mapToDto(FicheMesure fiche) {
        return new FicheMesureDto(
                fiche.getId(),
                fiche.getNomProjet(),
                fiche.getDatePrise(),
                fiche.getValeurs(), // Hibernate convertit le JSONB en Map ici
                fiche.getClient().getId()
        );
    }
}
