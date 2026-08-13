package com.mbaigo.datecentre.swingApp.services.impl;


import com.mbaigo.datecentre.swingApp.dto.ClientRequestDTO;
import com.mbaigo.datecentre.swingApp.dto.ClientResponseDTO;
import com.mbaigo.datecentre.swingApp.dto.mappers.ClientMapper;
import com.mbaigo.datecentre.swingApp.models.Client;
import com.mbaigo.datecentre.swingApp.repositories.ClientRepository;
import com.mbaigo.datecentre.swingApp.services.ClientService;
import com.mbaigo.datecentre.swingApp.utilities.Util;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {


    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper; // Injection du Mapper

   /* private String normaliserTelephone(String telephone) {
        if (telephone == null) return null;
        return telephone.replaceAll("[^\\d+]", "");
    }*/

    @Override
    @Transactional
    public ClientResponseDTO createClient(ClientRequestDTO dto) {
        String telNormalise = Util.normalizePhone(dto.telephone());

        if (clientRepository.existsByTelephone(telNormalise)) {
            throw new IllegalArgumentException("Un client avec le numéro " + telNormalise + " existe déjà.");
        }

        // Appel propre au mapper
        Client client = clientMapper.toEntity(dto, telNormalise);
        Client savedClient = clientRepository.save(client);

        return clientMapper.toDto(savedClient);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClientResponseDTO> getClientByTelephone(String telephone) {
        String telNormalise = Util.normalizePhone(telephone);

        return Optional.of(clientRepository.findByTelephone(telNormalise)
                .map(clientMapper::toDto) // Utilisation élégante avec les method references
                .orElseThrow(() -> new EntityNotFoundException("Aucun client trouvé avec le numéro : " + telNormalise)));
    }

    @Override
    @Transactional
    public ClientResponseDTO updateClient(Long id, ClientRequestDTO dto) {
        Client clientExistant = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable (ID: " + id + ")"));

        String nouveauTelNormalise = Util.normalizePhone(dto.telephone());

        if (!clientExistant.getTelephone().equals(nouveauTelNormalise)
                && clientRepository.existsByTelephone(nouveauTelNormalise)) {
            throw new IllegalArgumentException("Le numéro " + nouveauTelNormalise + " est déjà utilisé.");
        }

        // Le mapper s'occupe d'écraser les anciennes valeurs par les nouvelles
        clientMapper.updateEntityFromDto(clientExistant, dto, nouveauTelNormalise);

        return clientMapper.toDto(clientRepository.save(clientExistant));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientResponseDTO> getAllClients(int page, int size) {
        // On crée la requête de pagination (Page 0 par défaut, triée par nom de A à Z)
        Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
        return clientRepository.findAll(pageable)
                .map(clientMapper::toDto);
    }

    @Override
    public Optional<ClientResponseDTO> getClientById(Long id) {
        return clientRepository.findById(id).map(clientMapper::toDto);
    }

}
