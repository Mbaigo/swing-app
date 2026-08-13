package com.mbaigo.datecentre.swingApp.services;

import com.mbaigo.datecentre.swingApp.dto.ClientDto;
import com.mbaigo.datecentre.swingApp.dto.ClientRequestDTO;
import com.mbaigo.datecentre.swingApp.dto.ClientResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    // US 1.1
    ClientResponseDTO createClient(ClientRequestDTO requestDTO);
    // US 1.2
    Optional<ClientResponseDTO>  getClientByTelephone(String telephone);
    // US 1.3
    ClientResponseDTO updateClient(Long id, ClientRequestDTO requestDTO);

    Page<ClientResponseDTO> getAllClients(int page, int size);

    Optional<ClientResponseDTO> getClientById(Long id);
}
