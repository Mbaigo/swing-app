package com.mbaigo.datecentre.swingApp.dto.mappers;

import com.mbaigo.datecentre.swingApp.dto.CommandeResponseDTO;
import com.mbaigo.datecentre.swingApp.dto.LigneCommandeResponseDTO;
import com.mbaigo.datecentre.swingApp.models.Commande;
import com.mbaigo.datecentre.swingApp.models.ImageMaquette;
import com.mbaigo.datecentre.swingApp.models.LigneCommande;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
@Mapper(componentModel = "spring")
public interface CommandeMapper {

    CommandeResponseDTO toResponse(Commande entity);

    // On map la liste d'entités "images" vers la liste de String "imagesUrl"
    @Mapping(target = "imagesUrl", source = "images")
    LigneCommandeResponseDTO toLigneResponse(LigneCommande entity);

    // MapStruct utilisera automatiquement cette méthode pour faire la conversion
    default List<String> mapImages(List<ImageMaquette> images) {
        if (images == null || images.isEmpty()) {
            return Collections.emptyList();
        }
        return images.stream()
                .map(ImageMaquette::getUrl)
                .collect(Collectors.toList());
    }
}
