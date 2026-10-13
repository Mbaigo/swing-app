package com.mbaigo.datecentre.swingApp.services;

import com.mbaigo.datecentre.swingApp.dto.DashboardResponseDTO;

import java.time.LocalDate;

public interface DashboardService {
    public DashboardResponseDTO getDashboardData(LocalDate debut, LocalDate fin);
}
