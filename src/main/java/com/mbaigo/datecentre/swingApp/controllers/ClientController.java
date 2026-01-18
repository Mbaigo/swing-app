package com.mbaigo.datecentre.swingApp.controllers;

import com.mbaigo.datecentre.swingApp.dto.ClientDto;
import com.mbaigo.datecentre.swingApp.services.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Endpoints pour la gestion des clients et de leurs coordonnées")
@CrossOrigin(origins = "http://localhost:5173") // Pour ton futur Frontend Vue.js
public class ClientController {

    private final ClientService clientService;

    @Operation(summary = "Lister tous les clients", description = "Retourne la liste complète des clients enregistrés.")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllClients() {
        List<ClientDto> clients = clientService.getAllClients();

        if (clients.isEmpty()) {
            // 204 No Content est souvent préférable si la liste est vide,
            // mais 200 OK avec tableau vide est aussi standard. À toi de choisir.
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(clients); // 200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClientById(@PathVariable Long id) {
        // Le service lance une exception si pas trouvé, gérée par un @ControllerAdvice (voir plus bas)
        // ou on peut faire un try/catch ici, mais le mieux est de laisser le service gérer la logique.
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @Operation(summary = "Créer un nouveau client", description = "Enregistre un client avec validation des données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou téléphone déjà existant")
    })
    @PostMapping
    public ResponseEntity<Long> createClient(@RequestBody @Valid ClientDto clientDto) {
        Long id = clientService.createClient(clientDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(id); // 201 Created
    }

    @Operation(summary = "Rechercher par téléphone", description = "Retrouve la fiche d'un client via son numéro exact.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client trouvé"),
            @ApiResponse(responseCode = "404", description = "Numéro inconnu")
    })
    @GetMapping("/search")
    public ResponseEntity<Optional<ClientDto>> getClientByPhone(
            @Parameter(description = "Le numéro de téléphone exact (ex: 0612345678)", required = true)
            @RequestParam("phone") String telephone) {

        return ResponseEntity.ok(clientService.getByPhone(telephone));
    }
}
