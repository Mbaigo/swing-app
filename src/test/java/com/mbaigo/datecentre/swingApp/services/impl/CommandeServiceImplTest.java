package com.mbaigo.datecentre.swingApp.services.impl;

import com.mbaigo.datecentre.swingApp.dto.CommandeRequestDTO;
import com.mbaigo.datecentre.swingApp.dto.CommandeResponseDTO;
import com.mbaigo.datecentre.swingApp.dto.LigneCommandeRequestDTO;
import com.mbaigo.datecentre.swingApp.dto.mappers.CommandeMapper;
import com.mbaigo.datecentre.swingApp.enums.StatutCommande;
import com.mbaigo.datecentre.swingApp.models.*;
import com.mbaigo.datecentre.swingApp.repositories.ClientRepository;
import com.mbaigo.datecentre.swingApp.repositories.CommandeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandeServiceImplTest {

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private CommandeMapper mapper;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private CommandeServiceImpl commandeService;

    private Client client;
    private Commande commande;
    private CommandeRequestDTO commandeRequestDTO;
    private CommandeResponseDTO commandeResponseDTO;
    private LigneCommandeRequestDTO ligneCommandeRequestDTO;

    @BeforeEach
    void setUp() {
        // Initialisation du client
        client = new Client();
        client.setId(1L);
        client.setNom("Client Test");

        // Initialisation de la ligne de commande DTO
        ligneCommandeRequestDTO = new LigneCommandeRequestDTO(
                "Maquette Test",
                2,
                100.0,
                List.of("https://image1.jpg", "https://image2.jpg")
        );

        // Initialisation du DTO de requête
        commandeRequestDTO = new CommandeRequestDTO(
                1L,
                LocalDate.now().plusDays(7),
                List.of(ligneCommandeRequestDTO),
                null
        );

        // Initialisation de la commande
        commande = new Commande();
        commande.setId(1L);
        commande.setReference("CMD-12345");
        commande.setDateCommande(LocalDateTime.now());
        commande.setDateLivraison(LocalDate.now().plusDays(7));
        commande.setStatut(StatutCommande.CREEE);
        commande.setClient(client);
        commande.setCoutTotal(200.0);

        // Initialisation du DTO de réponse
        commandeResponseDTO = new CommandeResponseDTO(
                1L,
                "CMD-12345",
                LocalDateTime.now(),
                LocalDate.now().plusDays(7),
                StatutCommande.CREEE,
                200.0,
                1L,
                "Client Test",
                new ArrayList<>()
        );
    }

    // ==================== TESTS POUR creerCommande ====================

    @Test
    void creerCommande_AvecDonneesValides_DoitReussir() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(commandeRepository.save(any(Commande.class))).thenReturn(commande);
        when(mapper.toResponse(any(Commande.class))).thenReturn(commandeResponseDTO);

        // When
        CommandeResponseDTO result = commandeService.creerCommande(commandeRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("CMD-12345", result.reference());
        assertEquals(StatutCommande.CREEE, result.statut());

        verify(clientRepository, times(1)).findById(1L);
        verify(commandeRepository, times(1)).save(any(Commande.class));
        verify(mapper, times(1)).toResponse(any(Commande.class));
    }

    @Test
    void creerCommande_AvecClientInexistant_DoitLancerException() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            commandeService.creerCommande(commandeRequestDTO);
        });

        verify(clientRepository, times(1)).findById(1L);
        verify(commandeRepository, never()).save(any(Commande.class));
    }

    @Test
    void creerCommande_AvecPlusieursLignes_DoitCreerToutesLesLignes() {
        // Given
        LigneCommandeRequestDTO ligne2 = new LigneCommandeRequestDTO(
                "Maquette Test 2",
                3,
                150.0,
                List.of("https://image3.jpg")
        );
        
        CommandeRequestDTO requestAvecDeuxLignes = new CommandeRequestDTO(
                1L,
                LocalDate.now().plusDays(7),
                List.of(ligneCommandeRequestDTO, ligne2),
                null
        );

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(commandeRepository.save(any(Commande.class))).thenReturn(commande);
        when(mapper.toResponse(any(Commande.class))).thenReturn(commandeResponseDTO);

        // When
        CommandeResponseDTO result = commandeService.creerCommande(requestAvecDeuxLignes);

        // Then
        assertNotNull(result);
        verify(commandeRepository, times(1)).save(any(Commande.class));
    }

    @Test
    void creerCommande_SansImages_DoitReussir() {
        // Given
        LigneCommandeRequestDTO ligneSansImages = new LigneCommandeRequestDTO(
                "Maquette Sans Images",
                1,
                50.0,
                null
        );
        
        CommandeRequestDTO requestSansImages = new CommandeRequestDTO(
                1L,
                LocalDate.now().plusDays(7),
                List.of(ligneSansImages),
                null
        );

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(commandeRepository.save(any(Commande.class))).thenReturn(commande);
        when(mapper.toResponse(any(Commande.class))).thenReturn(commandeResponseDTO);

        // When
        CommandeResponseDTO result = commandeService.creerCommande(requestSansImages);

        // Then
        assertNotNull(result);
        verify(commandeRepository, times(1)).save(any(Commande.class));
    }

    // ==================== TESTS POUR getCommandeById ====================

    @Test
    void getCommandeById_AvecIdExistant_DoitRetournerCommande() {
        // Given
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(mapper.toResponse(commande)).thenReturn(commandeResponseDTO);

        // When
        CommandeResponseDTO result = commandeService.getCommandeById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("CMD-12345", result.reference());

        verify(commandeRepository, times(1)).findById(1L);
        verify(mapper, times(1)).toResponse(commande);
    }

    @Test
    void getCommandeById_AvecIdInexistant_DoitLancerException() {
        // Given
        when(commandeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            commandeService.getCommandeById(999L);
        });

        assertTrue(exception.getMessage().contains("Commande introuvable avec l'ID: 999"));
        verify(commandeRepository, times(1)).findById(999L);
    }

    // ==================== TESTS POUR mettreAJourStatut ====================

    @Test
    void mettreAJourStatut_AvecStatutValide_DoitMettreAJour() {
        // Given
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(mapper.toResponse(commande)).thenReturn(commandeResponseDTO);

        // When
        CommandeResponseDTO result = commandeService.mettreAJourStatut(1L, StatutCommande.EN_COURS);

        // Then
        assertNotNull(result);
        assertEquals(StatutCommande.EN_COURS, commande.getStatut());

        verify(commandeRepository, times(1)).findById(1L);
        verify(mapper, times(1)).toResponse(commande);
    }

    @Test
    void mettreAJourStatut_AvecCommandeInexistante_DoitLancerException() {
        // Given
        when(commandeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            commandeService.mettreAJourStatut(999L, StatutCommande.EN_COURS);
        });

        verify(commandeRepository, times(1)).findById(999L);
    }

    @Test
    void mettreAJourStatut_AvecTousLesStatuts_DoitReussir() {
        // Given
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(mapper.toResponse(commande)).thenReturn(commandeResponseDTO);

        // Test tous les statuts possibles
        for (StatutCommande statut : StatutCommande.values()) {
            // When
            commandeService.mettreAJourStatut(1L, statut);

            // Then
            assertEquals(statut, commande.getStatut());
        }
    }

    // ==================== TESTS POUR updateCommande ====================

    @Test
    void updateCommande_AvecDonneesValides_DoitMettreAJour() {
        // Given
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(commandeRepository.save(any(Commande.class))).thenReturn(commande);
        when(mapper.toResponse(any(Commande.class))).thenReturn(commandeResponseDTO);

        // When
        CommandeResponseDTO result = commandeService.updateCommande(1L, commandeRequestDTO);

        // Then
        assertNotNull(result);
        verify(commandeRepository, times(1)).findById(1L);
        verify(clientRepository, times(1)).findById(1L);
        verify(commandeRepository, times(1)).save(any(Commande.class));
    }

    @Test
    void updateCommande_AvecCommandeInexistante_DoitLancerException() {
        // Given
        when(commandeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            commandeService.updateCommande(999L, commandeRequestDTO);
        });

        assertTrue(exception.getMessage().contains("Commande introuvable avec l'ID : 999"));
        verify(commandeRepository, times(1)).findById(999L);
    }

    @Test
    void updateCommande_AvecClientInexistant_DoitLancerException() {
        // Given
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            commandeService.updateCommande(1L, commandeRequestDTO);
        });

        assertTrue(exception.getMessage().contains("Client introuvable avec l'ID : 1"));
        verify(commandeRepository, times(1)).findById(1L);
        verify(clientRepository, times(1)).findById(1L);
    }

    @Test
    void updateCommande_AvecNouveauStatut_DoitMettreAJourStatut() {
        // Given
        CommandeRequestDTO requestAvecStatut = new CommandeRequestDTO(
                1L,
                LocalDate.now().plusDays(7),
                List.of(ligneCommandeRequestDTO),
                StatutCommande.EN_COURS
        );

        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(commandeRepository.save(any(Commande.class))).thenReturn(commande);
        when(mapper.toResponse(any(Commande.class))).thenReturn(commandeResponseDTO);

        // When
        commandeService.updateCommande(1L, requestAvecStatut);

        // Then
        assertEquals(StatutCommande.EN_COURS, commande.getStatut());
    }

    // ==================== TESTS POUR supprimerCommande ====================

    @Test
    void supprimerCommande_AvecIdExistant_DoitSupprimer() {
        // Given
        when(commandeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(commandeRepository).deleteById(1L);

        // When
        commandeService.supprimerCommande(1L);

        // Then
        verify(commandeRepository, times(1)).existsById(1L);
        verify(commandeRepository, times(1)).deleteById(1L);
    }

    @Test
    void supprimerCommande_AvecIdInexistant_DoitLancerException() {
        // Given
        when(commandeRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            commandeService.supprimerCommande(999L);
        });

        verify(commandeRepository, times(1)).existsById(999L);
        verify(commandeRepository, never()).deleteById(anyLong());
    }

    // ==================== TESTS POUR getAllCommandes ====================

    @Test
    void getAllCommandes_DoitRetournerPageDeCommandes() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Commande> commandes = List.of(commande);
        Page<Commande> commandePage = new PageImpl<>(commandes, pageable, 1);

        when(commandeRepository.findAll(pageable)).thenReturn(commandePage);
        when(mapper.toResponse(commande)).thenReturn(commandeResponseDTO);

        // When
        Page<CommandeResponseDTO> result = commandeService.getAllCommandes(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        verify(commandeRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllCommandes_SansCommandes_DoitRetournerPageVide() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Commande> commandePage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(commandeRepository.findAll(pageable)).thenReturn(commandePage);

        // When
        Page<CommandeResponseDTO> result = commandeService.getAllCommandes(pageable);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    // ==================== TESTS POUR getCommandesParJour ====================

    @Test
    void getCommandesParJour_AvecDateValide_DoitRetournerCommandes() {
        // Given
        LocalDate date = LocalDate.of(2026, 6, 1);
        Pageable pageable = PageRequest.of(0, 10);
        
        LocalDateTime debutJournee = date.atStartOfDay();
        LocalDateTime finJournee = date.atTime(LocalTime.MAX);

        List<Commande> commandes = List.of(commande);
        Page<Commande> commandePage = new PageImpl<>(commandes, pageable, 1);

        when(commandeRepository.findByDateCommandeBetween(debutJournee, finJournee, pageable))
                .thenReturn(commandePage);
        when(mapper.toResponse(commande)).thenReturn(commandeResponseDTO);

        // When
        Page<CommandeResponseDTO> result = commandeService.getCommandesParJour(date, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(commandeRepository, times(1)).findByDateCommandeBetween(debutJournee, finJournee, pageable);
    }

    // ==================== TESTS POUR getCommandesParSemaine ====================

    @Test
    void getCommandesParSemaine_AvecDateEnSemaine_DoitRetournerCommandes() {
        // Given - Un mercredi
        LocalDate mercredi = LocalDate.of(2026, 6, 3);
        Pageable pageable = PageRequest.of(0, 10);
        
        LocalDate lundi = LocalDate.of(2026, 6, 1);
        LocalDate dimanche = LocalDate.of(2026, 6, 7);
        
        LocalDateTime debutSemaine = lundi.atStartOfDay();
        LocalDateTime finSemaine = dimanche.atTime(LocalTime.MAX);

        List<Commande> commandes = List.of(commande);
        Page<Commande> commandePage = new PageImpl<>(commandes, pageable, 1);

        when(commandeRepository.findByDateCommandeBetween(debutSemaine, finSemaine, pageable))
                .thenReturn(commandePage);
        when(mapper.toResponse(commande)).thenReturn(commandeResponseDTO);

        // When
        Page<CommandeResponseDTO> result = commandeService.getCommandesParSemaine(mercredi, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(commandeRepository, times(1)).findByDateCommandeBetween(debutSemaine, finSemaine, pageable);
    }

    @Test
    void getCommandesParSemaine_AvecLundi_DoitRetournerSemaine() {
        // Given - Un lundi
        LocalDate lundi = LocalDate.of(2026, 6, 1);
        Pageable pageable = PageRequest.of(0, 10);
        
        LocalDate dimanche = LocalDate.of(2026, 6, 7);
        
        LocalDateTime debutSemaine = lundi.atStartOfDay();
        LocalDateTime finSemaine = dimanche.atTime(LocalTime.MAX);

        Page<Commande> commandePage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(commandeRepository.findByDateCommandeBetween(debutSemaine, finSemaine, pageable))
                .thenReturn(commandePage);

        // When
        Page<CommandeResponseDTO> result = commandeService.getCommandesParSemaine(lundi, pageable);

        // Then
        assertNotNull(result);
        verify(commandeRepository, times(1)).findByDateCommandeBetween(debutSemaine, finSemaine, pageable);
    }

    @Test
    void getCommandesParSemaine_AvecDimanche_DoitRetournerSemaine() {
        // Given - Un dimanche
        LocalDate dimanche = LocalDate.of(2026, 6, 7);
        Pageable pageable = PageRequest.of(0, 10);
        
        LocalDate lundi = LocalDate.of(2026, 6, 1);
        
        LocalDateTime debutSemaine = lundi.atStartOfDay();
        LocalDateTime finSemaine = dimanche.atTime(LocalTime.MAX);

        Page<Commande> commandePage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(commandeRepository.findByDateCommandeBetween(debutSemaine, finSemaine, pageable))
                .thenReturn(commandePage);

        // When
        Page<CommandeResponseDTO> result = commandeService.getCommandesParSemaine(dimanche, pageable);

        // Then
        assertNotNull(result);
        verify(commandeRepository, times(1)).findByDateCommandeBetween(debutSemaine, finSemaine, pageable);
    }

    // ==================== TESTS POUR getCommandesALivrerAujourdhui ====================

    @Test
    void getCommandesALivrerAujourdhui_DoitRetournerCommandesDuJour() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Commande> commandes = List.of(commande);
        Page<Commande> commandePage = new PageImpl<>(commandes, pageable, 1);

        when(commandeRepository.findByDateLivraison(any(LocalDate.class), eq(pageable)))
                .thenReturn(commandePage);
        when(mapper.toResponse(commande)).thenReturn(commandeResponseDTO);

        // When
        Page<CommandeResponseDTO> result = commandeService.getCommandesALivrerAujourdhui(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(commandeRepository, times(1)).findByDateLivraison(any(LocalDate.class), eq(pageable));
    }

    // ==================== TESTS POUR getCommandesALivrerCetteSemaine ====================

    @Test
    void getCommandesALivrerCetteSemaine_DoitRetournerCommandesDeLaSemaine() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Commande> commandes = List.of(commande);
        Page<Commande> commandePage = new PageImpl<>(commandes, pageable, 1);

        when(commandeRepository.findByDateLivraisonBetween(any(LocalDate.class), any(LocalDate.class), eq(pageable)))
                .thenReturn(commandePage);
        when(mapper.toResponse(commande)).thenReturn(commandeResponseDTO);

        // When
        Page<CommandeResponseDTO> result = commandeService.getCommandesALivrerCetteSemaine(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(commandeRepository, times(1))
                .findByDateLivraisonBetween(any(LocalDate.class), any(LocalDate.class), eq(pageable));
    }

    // ==================== TESTS POUR getCommandesParStatut ====================

    @Test
    void getCommandesParStatut_AvecStatutCREEE_DoitRetournerCommandes() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Commande> commandes = List.of(commande);
        Page<Commande> commandePage = new PageImpl<>(commandes, pageable, 1);

        when(commandeRepository.findByStatut(StatutCommande.CREEE, pageable)).thenReturn(commandePage);
        when(mapper.toResponse(commande)).thenReturn(commandeResponseDTO);

        // When
        Page<CommandeResponseDTO> result = commandeService.getCommandesParStatut(StatutCommande.CREEE, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(commandeRepository, times(1)).findByStatut(StatutCommande.CREEE, pageable);
    }

    @Test
    void getCommandesParStatut_AvecTousLesStatuts_DoitReussir() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Commande> commandePage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(commandeRepository.findByStatut(any(StatutCommande.class), eq(pageable))).thenReturn(commandePage);

        // Test tous les statuts
        for (StatutCommande statut : StatutCommande.values()) {
            // When
            Page<CommandeResponseDTO> result = commandeService.getCommandesParStatut(statut, pageable);

            // Then
            assertNotNull(result);
        }
    }

    // ==================== TESTS POUR getCommandesParPeriode ====================

    @Test
    void getCommandesParPeriode_AvecPeriodeValide_DoitRetournerCommandes() {
        // Given
        LocalDateTime dateDebut = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime dateFin = LocalDateTime.of(2026, 12, 31, 23, 59);
        Pageable pageable = PageRequest.of(0, 10);

        List<Commande> commandes = List.of(commande);
        Page<Commande> commandePage = new PageImpl<>(commandes, pageable, 1);

        when(commandeRepository.findByDateCommandeBetween(dateDebut, dateFin, pageable))
                .thenReturn(commandePage);
        when(mapper.toResponse(commande)).thenReturn(commandeResponseDTO);

        // When
        Page<CommandeResponseDTO> result = commandeService.getCommandesParPeriode(dateDebut, dateFin, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(commandeRepository, times(1)).findByDateCommandeBetween(dateDebut, dateFin, pageable);
    }

    // ==================== TESTS POUR getCommandesParMois ====================

    @Test
    void getCommandesParMois_AvecJanvier2026_DoitRetournerCommandes() {
        // Given
        int annee = 2026;
        int mois = 1;
        Pageable pageable = PageRequest.of(0, 10);

        LocalDateTime debutMois = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
        LocalDateTime finMois = LocalDateTime.of(2026, 1, 31, 23, 59, 59, 999999999);

        List<Commande> commandes = List.of(commande);
        Page<Commande> commandePage = new PageImpl<>(commandes, pageable, 1);

        when(commandeRepository.findByDateCommandeBetween(debutMois, finMois, pageable))
                .thenReturn(commandePage);
        when(mapper.toResponse(commande)).thenReturn(commandeResponseDTO);

        // When
        Page<CommandeResponseDTO> result = commandeService.getCommandesParMois(annee, mois, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(commandeRepository, times(1)).findByDateCommandeBetween(debutMois, finMois, pageable);
    }

    @Test
    void getCommandesParMois_AvecFevrier2024_DoitGererAnneeBissextile() {
        // Given - 2024 est une année bissextile
        int annee = 2024;
        int mois = 2;
        Pageable pageable = PageRequest.of(0, 10);

        LocalDateTime debutMois = LocalDateTime.of(2024, 2, 1, 0, 0, 0);
        LocalDateTime finMois = LocalDateTime.of(2024, 2, 29, 23, 59, 59, 999999999);

        Page<Commande> commandePage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(commandeRepository.findByDateCommandeBetween(debutMois, finMois, pageable))
                .thenReturn(commandePage);

        // When
        Page<CommandeResponseDTO> result = commandeService.getCommandesParMois(annee, mois, pageable);

        // Then
        assertNotNull(result);
        verify(commandeRepository, times(1)).findByDateCommandeBetween(debutMois, finMois, pageable);
    }

    @Test
    void getCommandesParMois_AvecFevrier2023_DoitGererAnneeNonBissextile() {
        // Given - 2023 n'est pas une année bissextile
        int annee = 2023;
        int mois = 2;
        Pageable pageable = PageRequest.of(0, 10);

        LocalDateTime debutMois = LocalDateTime.of(2023, 2, 1, 0, 0, 0);
        LocalDateTime finMois = LocalDateTime.of(2023, 2, 28, 23, 59, 59, 999999999);

        Page<Commande> commandePage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(commandeRepository.findByDateCommandeBetween(debutMois, finMois, pageable))
                .thenReturn(commandePage);

        // When
        Page<CommandeResponseDTO> result = commandeService.getCommandesParMois(annee, mois, pageable);

        // Then
        assertNotNull(result);
        verify(commandeRepository, times(1)).findByDateCommandeBetween(debutMois, finMois, pageable);
    }

    @Test
    void getCommandesParMois_AvecDecembre_DoitGerer31Jours() {
        // Given
        int annee = 2026;
        int mois = 12;
        Pageable pageable = PageRequest.of(0, 10);

        LocalDateTime debutMois = LocalDateTime.of(2026, 12, 1, 0, 0, 0);
        LocalDateTime finMois = LocalDateTime.of(2026, 12, 31, 23, 59, 59, 999999999);

        Page<Commande> commandePage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(commandeRepository.findByDateCommandeBetween(debutMois, finMois, pageable))
                .thenReturn(commandePage);

        // When
        Page<CommandeResponseDTO> result = commandeService.getCommandesParMois(annee, mois, pageable);

        // Then
        assertNotNull(result);
        verify(commandeRepository, times(1)).findByDateCommandeBetween(debutMois, finMois, pageable);
    }
}
