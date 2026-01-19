package com.mbaigo.datecentre.swingApp.services;

import com.mbaigo.datecentre.swingApp.dto.FicheMesureDto;

import java.util.List;

public interface FicheMesureService {
    Long createFiche(FicheMesureDto dto);
    List<FicheMesureDto> getFichesByClient(Long clientId);
    FicheMesureDto getFicheById(Long id);
    FicheMesureDto updateFiche(Long id, FicheMesureDto dto);
}
