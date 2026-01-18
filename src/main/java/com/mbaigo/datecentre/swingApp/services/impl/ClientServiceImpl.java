package com.mbaigo.datecentre.swingApp.services.impl;


import com.mbaigo.datecentre.swingApp.dto.ClientDto;
import com.mbaigo.datecentre.swingApp.model.Client;
import com.mbaigo.datecentre.swingApp.repositories.ClientRepository;
import com.mbaigo.datecentre.swingApp.services.ClientService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.mbaigo.datecentre.swingApp.utilities.Util.normalizePhone;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

        private final ClientRepository clientRepository;

        public List<ClientDto> getAllClients() {
            return clientRepository.findAll().stream()
                    .map(this::mapToDto)
                    .toList();
        }

        @Transactional
        public Long createClient(ClientDto dto) {
            if (clientRepository.existsByTelephone(dto.telephone())) {
                throw new IllegalArgumentException("Un client avec ce numero de téléphone existe déjà.");
            }

            Client client = Client.builder()
                    .nom(dto.nom())
                    .prenom(dto.prenom())
                    .telephone(normalizePhone(dto.telephone()))
                    .email(dto.email())
                    .genre(dto.genre())
                    .notesMorphologie(dto.notesMorphologie())
                    .build();

            return clientRepository.save(client).getId();
        }

    @Override
    public ClientDto getClientById(Long id) {
        return null;
    }

    @Override
    public Optional<ClientDto> getByPhone(String phoneNumber) {
        String cleanPhone = normalizePhone(phoneNumber);
            return Optional.ofNullable(clientRepository.findByTelephone(cleanPhone)
                    .map(this::mapToDto)
                    .orElseThrow(() -> new EntityNotFoundException("Aucun client trouvé avec le numéro : " + phoneNumber)));
    }

    // Petit mapper manuel (pour éviter MapStruct pour l'instant)
        private ClientDto mapToDto(Client client) {
            return new ClientDto(
                    client.getId(),
                    client.getNom(),
                    client.getPrenom(),
                    client.getTelephone(),
                    client.getEmail(),
                    client.getGenre(),
                    client.getNotesMorphologie()
            );
        }
    }
