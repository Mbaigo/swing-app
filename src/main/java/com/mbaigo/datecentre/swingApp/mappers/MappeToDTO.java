package com.mbaigo.datecentre.swingApp.mappers;

import com.mbaigo.datecentre.swingApp.dto.ClientDto;
import com.mbaigo.datecentre.swingApp.dto.FicheMesureDto;
import com.mbaigo.datecentre.swingApp.model.Client;
import com.mbaigo.datecentre.swingApp.model.FicheMesure;

public class MappeToDTO {

    public static FicheMesureDto mapFicheMesureToDto(FicheMesure fiche) {
        return new FicheMesureDto(
                fiche.getId(),
                fiche.getNomProjet(),
                fiche.getDatePrise(),
                fiche.getValeurs(), // Hibernate convertit le JSONB en Map ici
                fiche.getClient().getId()
        );
    }

    public static ClientDto mapClientToDto(Client client) {
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
