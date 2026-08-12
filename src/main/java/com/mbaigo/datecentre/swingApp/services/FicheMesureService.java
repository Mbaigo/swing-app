package com.mbaigo.datecentre.swingApp.services;

import com.mbaigo.datecentre.swingApp.dto.FicheMesureDto;
import com.mbaigo.datecentre.swingApp.dto.FicheMesureRequestDTO;
import com.mbaigo.datecentre.swingApp.dto.FicheMesureResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FicheMesureService {
    Long createFiche(FicheMesureDto dto);
    List<FicheMesureDto> getFichesByClient(Long clientId);
    FicheMesureDto getFicheById(Long id);
    FicheMesureDto updateFiche(Long id, FicheMesureDto dto);

    FicheMesureResponseDTO createFicheMesure(FicheMesureRequestDTO requestDTO);
    List<FicheMesureResponseDTO> getFichesByClientId(Long clientId);
    FicheMesureResponseDTO getFicheById(Long id);
    // ... (autres méthodes)
    FicheMesureResponseDTO updateFicheMesure(Long id, FicheMesureRequestDTO requestDTO);
    Page<FicheMesureResponseDTO> getAllFiches(int page, int size);
}
