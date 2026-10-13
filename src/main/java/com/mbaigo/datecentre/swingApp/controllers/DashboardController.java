package com.mbaigo.datecentre.swingApp.controllers;

import com.mbaigo.datecentre.swingApp.dto.DashboardResponseDTO;
import com.mbaigo.datecentre.swingApp.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponseDTO> getOverview() {
        return ResponseEntity.ok(dashboardService.getDashboardData());
    }
}
