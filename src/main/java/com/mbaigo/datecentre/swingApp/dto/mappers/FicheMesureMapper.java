package com.mbaigo.datecentre.swingApp.dto.mappers;


import com.mbaigo.datecentre.swingApp.dto.FicheMesureRequestDTO;
import com.mbaigo.datecentre.swingApp.dto.FicheMesureResponseDTO;
import com.mbaigo.datecentre.swingApp.models.Client;
import com.mbaigo.datecentre.swingApp.models.FicheMesure;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FicheMesureMapper {

    // On dit à MapStruct d'aller chercher l'ID du client pour le mettre dans le DTO
    @Mapping(target = "clientId", source = "entity.client.id")
    @Mapping(target = "clientNom", source = "entity.client.nom")
    @Mapping(target = "clientPrenom", source = "entity.client.prenom")
    FicheMesureResponseDTO toDto(FicheMesure entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", source = "client")
    //@Mapping(target = "datePrise", ignore = true)
    FicheMesure toEntity(FicheMesureRequestDTO dto, Client client);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true) // Le client ne change pas lors d'une mise à jour
    //@Mapping(target = "datePrise", ignore = true)
    void updateEntityFromDto(@MappingTarget FicheMesure fiche, FicheMesureRequestDTO dto);
}