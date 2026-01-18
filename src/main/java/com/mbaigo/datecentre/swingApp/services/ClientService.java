package com.mbaigo.datecentre.swingApp.services;

import com.mbaigo.datecentre.swingApp.dto.ClientDto;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    /**
     * Récupère la liste complète des clients.
     * @return liste de DTOs
     */
    List<ClientDto> getAllClients();

    /**
     * Crée un nouveau client après validation métier.
     * @param dto les données du client
     * @return l'ID du client créé
     */
    Long createClient(ClientDto dto);

    // Bonne pratique : on ajoute souvent la méthode de lecture unitaire
    ClientDto getClientById(Long id);

    //GetByPhoneNumber
    Optional<ClientDto> getByPhone(String phoneNumber);
}
