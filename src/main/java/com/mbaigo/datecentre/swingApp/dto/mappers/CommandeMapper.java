package com.mbaigo.datecentre.swingApp.dto.mappers;

import com.mbaigo.datecentre.swingApp.dto.CommandeResponseDTO;
import com.mbaigo.datecentre.swingApp.dto.LigneCommandeResponseDTO;
import com.mbaigo.datecentre.swingApp.models.Commande;
import com.mbaigo.datecentre.swingApp.models.LigneCommande;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommandeMapper {

    // Convertit la commande globale
    CommandeResponseDTO toResponse(Commande entity);

    // Convertit chaque ligne (MapStruct l'appelle automatiquement pour la liste)
    // On force l'exécution de la méthode getCoutLigne() de l'entité pour remplir le DTO
    //@Mapping(target = "quantite", expression = "java(entity.getCoutLigne())")
    LigneCommandeResponseDTO toLigneResponse(LigneCommande entity);

}
