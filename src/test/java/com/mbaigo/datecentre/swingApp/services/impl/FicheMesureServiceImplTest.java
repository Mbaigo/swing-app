package com.mbaigo.datecentre.swingApp.services.impl;

import com.mbaigo.datecentre.swingApp.dto.FicheMesureRequestDTO;
import com.mbaigo.datecentre.swingApp.dto.FicheMesureResponseDTO;
import com.mbaigo.datecentre.swingApp.dto.mappers.FicheMesureMapper;
import com.mbaigo.datecentre.swingApp.models.Client;
import com.mbaigo.datecentre.swingApp.models.FicheMesure;
import com.mbaigo.datecentre.swingApp.repositories.ClientRepository;
import com.mbaigo.datecentre.swingApp.repositories.FicheMesureRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FicheMesureServiceImplTest {

    @Mock
    private FicheMesureRepository ficheMesureRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private FicheMesureMapper ficheMesureMapper;

    @InjectMocks
    private FicheMesureServiceImpl ficheMesureService;

    private Client client;
    private FicheMesure ficheMesure;
    private FicheMesureRequestDTO ficheMesureRequestDTO;
    private FicheMesureResponseDTO ficheMesureResponseDTO;
    private Map<String, Object> mesures;

    @BeforeEach
    void setUp() {
        // Initialisation du client
        client = new Client();
        client.setId(1L);
        client.setNom("Diallo");
        client.setPrenom("Fatou");
        client.setTelephone("+221771234567");

        // Initialisation des mesures (JSONB)
        mesures = new HashMap<>();
        mesures.put("poitrine", 95.0);
        mesures.put("taille", 75.0);
        mesures.put("hanches", 100.0);
        mesures.put("longueur", 110.0);

        // Initialisation du DTO de requête
        ficheMesureRequestDTO = new FicheMesureRequestDTO(
                1L,
                mesures
        );

        // Initialisation de la fiche de mesure
        ficheMesure = new FicheMesure();
        ficheMesure.setId(1L);
        ficheMesure.setClient(client);
        ficheMesure.setMesures(mesures);
        ficheMesure.setDatePrise(LocalDateTime.now());

        // Initialisation du DTO de réponse
        ficheMesureResponseDTO = new FicheMesureResponseDTO(
                1L,
                1L,
                "Diallo",
                "Fatou",
                mesures,
                LocalDateTime.now()
        );
    }

    // ==================== TESTS POUR createFicheMesure ====================

    @Test
    void createFicheMesure_AvecClientExistant_DoitCreerFiche() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(ficheMesureMapper.toEntity(ficheMesureRequestDTO, client)).thenReturn(ficheMesure);
        when(ficheMesureRepository.save(ficheMesure)).thenReturn(ficheMesure);
        when(ficheMesureMapper.toDto(ficheMesure)).thenReturn(ficheMesureResponseDTO);

        // When
        FicheMesureResponseDTO result = ficheMesureService.createFicheMesure(ficheMesureRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(1L, result.clientId());
        assertEquals("Diallo", result.nomClient());
        assertEquals(mesures, result.mesures());

        verify(clientRepository, times(1)).findById(1L);
        verify(ficheMesureMapper, times(1)).toEntity(ficheMesureRequestDTO, client);
        verify(ficheMesureRepository, times(1)).save(ficheMesure);
        verify(ficheMesureMapper, times(1)).toDto(ficheMesure);
    }

    @Test
    void createFicheMesure_AvecClientInexistant_DoitLancerException() {
        // Given
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        FicheMesureRequestDTO requestAvecClientInconnu = new FicheMesureRequestDTO(999L, mesures);

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            ficheMesureService.createFicheMesure(requestAvecClientInconnu);
        });

        assertTrue(exception.getMessage().contains("Impossible de créer la fiche"));
        assertTrue(exception.getMessage().contains("999"));
        assertTrue(exception.getMessage().contains("n'existe pas"));

        verify(clientRepository, times(1)).findById(999L);
        verify(ficheMesureRepository, never()).save(any());
    }

    @Test
    void createFicheMesure_AvecMesuresVides_DoitReussir() {
        // Given
        Map<String, Object> mesuresVides = new HashMap<>();
        FicheMesureRequestDTO requestMesuresVides = new FicheMesureRequestDTO(1L, mesuresVides);

        FicheMesure ficheAvecMesuresVides = new FicheMesure();
        ficheAvecMesuresVides.setClient(client);
        ficheAvecMesuresVides.setMesures(mesuresVides);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(ficheMesureMapper.toEntity(requestMesuresVides, client)).thenReturn(ficheAvecMesuresVides);
        when(ficheMesureRepository.save(ficheAvecMesuresVides)).thenReturn(ficheAvecMesuresVides);
        when(ficheMesureMapper.toDto(ficheAvecMesuresVides)).thenReturn(
                new FicheMesureResponseDTO(1L, 1L, "Diallo", "Fatou", mesuresVides, LocalDateTime.now())
        );

        // When
        FicheMesureResponseDTO result = ficheMesureService.createFicheMesure(requestMesuresVides);

        // Then
        assertNotNull(result);
        assertTrue(result.mesures().isEmpty());
    }

    @Test
    void createFicheMesure_AvecMesuresComplexes_DoitReussir() {
        // Given
        Map<String, Object> mesuresComplexes = new HashMap<>();
        mesuresComplexes.put("poitrine", 95.0);
        mesuresComplexes.put("notes", "Client préfère les tissus légers");
        mesuresComplexes.put("type_vetement", "Robe");

        FicheMesureRequestDTO requestComplexe = new FicheMesureRequestDTO(1L, mesuresComplexes);

        FicheMesure ficheComplexe = new FicheMesure();
        ficheComplexe.setId(2L);
        ficheComplexe.setClient(client);
        ficheComplexe.setMesures(mesuresComplexes);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(ficheMesureMapper.toEntity(requestComplexe, client)).thenReturn(ficheComplexe);
        when(ficheMesureRepository.save(ficheComplexe)).thenReturn(ficheComplexe);
        when(ficheMesureMapper.toDto(ficheComplexe)).thenReturn(
                new FicheMesureResponseDTO(2L, 1L, "Diallo", "Fatou", mesuresComplexes, LocalDateTime.now())
        );

        // When
        FicheMesureResponseDTO result = ficheMesureService.createFicheMesure(requestComplexe);

        // Then
        assertNotNull(result);
        assertEquals(mesuresComplexes, result.mesures());
    }

    // ==================== TESTS POUR getFichesByClientId ====================

    @Test
    void getFichesByClientId_AvecClientExistantEtFiches_DoitRetournerListe() {
        // Given
        when(clientRepository.existsById(1L)).thenReturn(true);
        
        List<FicheMesure> fiches = List.of(ficheMesure);
        when(ficheMesureRepository.findByClientIdOrderByDatePriseDesc(1L)).thenReturn(fiches);
        when(ficheMesureMapper.toDto(ficheMesure)).thenReturn(ficheMesureResponseDTO);

        // When
        List<FicheMesureResponseDTO> result = ficheMesureService.getFichesByClientId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).clientId());
        assertEquals("Diallo", result.get(0).nomClient());

        verify(clientRepository, times(1)).existsById(1L);
        verify(ficheMesureRepository, times(1)).findByClientIdOrderByDatePriseDesc(1L);
        verify(ficheMesureMapper, times(1)).toDto(ficheMesure);
    }

    @Test
    void getFichesByClientId_AvecClientInexistant_DoitLancerException() {
        // Given
        when(clientRepository.existsById(999L)).thenReturn(false);

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            ficheMesureService.getFichesByClientId(999L);
        });

        assertTrue(exception.getMessage().contains("Client introuvable"));
        assertTrue(exception.getMessage().contains("999"));

        verify(clientRepository, times(1)).existsById(999L);
        verify(ficheMesureRepository, never()).findByClientIdOrderByDatePriseDesc(anyLong());
    }

    @Test
    void getFichesByClientId_AvecClientSansFiches_DoitRetournerListeVide() {
        // Given
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(ficheMesureRepository.findByClientIdOrderByDatePriseDesc(1L)).thenReturn(new ArrayList<>());

        // When
        List<FicheMesureResponseDTO> result = ficheMesureService.getFichesByClientId(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(ficheMesureRepository, times(1)).findByClientIdOrderByDatePriseDesc(1L);
    }

    @Test
    void getFichesByClientId_AvecPlusieursFiches_DoitRetournerToutesTriees() {
        // Given
        FicheMesure fiche1 = new FicheMesure();
        fiche1.setId(1L);
        fiche1.setClient(client);
        fiche1.setDatePrise(LocalDateTime.now().minusDays(1));

        FicheMesure fiche2 = new FicheMesure();
        fiche2.setId(2L);
        fiche2.setClient(client);
        fiche2.setDatePrise(LocalDateTime.now());

        when(clientRepository.existsById(1L)).thenReturn(true);
        when(ficheMesureRepository.findByClientIdOrderByDatePriseDesc(1L))
                .thenReturn(List.of(fiche2, fiche1)); // Plus récente en premier

        when(ficheMesureMapper.toDto(fiche2)).thenReturn(
                new FicheMesureResponseDTO(2L, 1L, "Diallo", "Fatou", mesures, fiche2.getDatePrise())
        );
        when(ficheMesureMapper.toDto(fiche1)).thenReturn(
                new FicheMesureResponseDTO(1L, 1L, "Diallo", "Fatou", mesures, fiche1.getDatePrise())
        );

        // When
        List<FicheMesureResponseDTO> result = ficheMesureService.getFichesByClientId(1L);

        // Then
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).id()); // Plus récente en premier
        assertEquals(1L, result.get(1).id());
    }

    // ==================== TESTS POUR updateFicheMesure ====================

    @Test
    void updateFicheMesure_AvecDonneesValides_DoitMettreAJour() {
        // Given
        Map<String, Object> nouvellesMesures = new HashMap<>();
        nouvellesMesures.put("poitrine", 97.0);
        nouvellesMesures.put("taille", 76.0);

        FicheMesureRequestDTO updateDto = new FicheMesureRequestDTO(1L, nouvellesMesures);

        when(ficheMesureRepository.findById(1L)).thenReturn(Optional.of(ficheMesure));
        doNothing().when(ficheMesureMapper).updateEntityFromDto(ficheMesure, updateDto);
        when(ficheMesureRepository.save(ficheMesure)).thenReturn(ficheMesure);
        when(ficheMesureMapper.toDto(ficheMesure)).thenReturn(ficheMesureResponseDTO);

        // When
        FicheMesureResponseDTO result = ficheMesureService.updateFicheMesure(1L, updateDto);

        // Then
        assertNotNull(result);
        verify(ficheMesureRepository, times(1)).findById(1L);
        verify(ficheMesureMapper, times(1)).updateEntityFromDto(ficheMesure, updateDto);
        verify(ficheMesureRepository, times(1)).save(ficheMesure);
    }

    @Test
    void updateFicheMesure_AvecFicheInexistante_DoitLancerException() {
        // Given
        when(ficheMesureRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            ficheMesureService.updateFicheMesure(999L, ficheMesureRequestDTO);
        });

        assertTrue(exception.getMessage().contains("Fiche de mesure introuvable"));
        assertTrue(exception.getMessage().contains("999"));

        verify(ficheMesureRepository, times(1)).findById(999L);
        verify(ficheMesureRepository, never()).save(any());
    }

    @Test
    void updateFicheMesure_AvecTransfertVersAutreClient_DoitLancerException() {
        // Given
        FicheMesureRequestDTO transfertDto = new FicheMesureRequestDTO(2L, mesures); // Client ID différent

        when(ficheMesureRepository.findById(1L)).thenReturn(Optional.of(ficheMesure));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ficheMesureService.updateFicheMesure(1L, transfertDto);
        });

        assertTrue(exception.getMessage().contains("Opération refusée"));
        assertTrue(exception.getMessage().contains("transférer"));

        verify(ficheMesureRepository, times(1)).findById(1L);
        verify(ficheMesureRepository, never()).save(any());
    }

    @Test
    void updateFicheMesure_AvecMemeClient_DoitReussir() {
        // Given
        ficheMesure.getClient().setId(1L); // Même client que dans le DTO

        when(ficheMesureRepository.findById(1L)).thenReturn(Optional.of(ficheMesure));
        doNothing().when(ficheMesureMapper).updateEntityFromDto(ficheMesure, ficheMesureRequestDTO);
        when(ficheMesureRepository.save(ficheMesure)).thenReturn(ficheMesure);
        when(ficheMesureMapper.toDto(ficheMesure)).thenReturn(ficheMesureResponseDTO);

        // When
        FicheMesureResponseDTO result = ficheMesureService.updateFicheMesure(1L, ficheMesureRequestDTO);

        // Then
        assertNotNull(result);
        verify(ficheMesureRepository, times(1)).save(ficheMesure);
    }

    // ==================== TESTS POUR getAllFiches ====================

    @Test
    void getAllFiches_AvecFiches_DoitRetournerPageTriee() {
        // Given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("datePrise").ascending());

        List<FicheMesure> fiches = List.of(ficheMesure);
        Page<FicheMesure> fichePage = new PageImpl<>(fiches, pageable, 1);

        when(ficheMesureRepository.findAll(pageable)).thenReturn(fichePage);
        when(ficheMesureMapper.toDto(ficheMesure)).thenReturn(ficheMesureResponseDTO);

        // When
        Page<FicheMesureResponseDTO> result = ficheMesureService.getAllFiches(page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        verify(ficheMesureRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllFiches_SansFiches_DoitRetournerPageVide() {
        // Given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("datePrise").ascending());
        Page<FicheMesure> fichePage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(ficheMesureRepository.findAll(pageable)).thenReturn(fichePage);

        // When
        Page<FicheMesureResponseDTO> result = ficheMesureService.getAllFiches(page, size);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void getAllFiches_AvecPaginationDifferente_DoitUtiliserBonsParametres() {
        // Given
        int page = 3;
        int size = 25;
        Pageable pageable = PageRequest.of(page, size, Sort.by("datePrise").ascending());
        Page<FicheMesure> fichePage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(ficheMesureRepository.findAll(pageable)).thenReturn(fichePage);

        // When
        Page<FicheMesureResponseDTO> result = ficheMesureService.getAllFiches(page, size);

        // Then
        assertNotNull(result);
        verify(ficheMesureRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllFiches_DoitTrierParDatePriseAscendant() {
        // Given
        FicheMesure ficheAncienne = new FicheMesure();
        ficheAncienne.setId(1L);
        ficheAncienne.setDatePrise(LocalDateTime.now().minusDays(5));

        FicheMesure ficheRecente = new FicheMesure();
        ficheRecente.setId(2L);
        ficheRecente.setDatePrise(LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 10, Sort.by("datePrise").ascending());
        Page<FicheMesure> fichePage = new PageImpl<>(
                List.of(ficheAncienne, ficheRecente), pageable, 2
        );

        when(ficheMesureRepository.findAll(pageable)).thenReturn(fichePage);
        when(ficheMesureMapper.toDto(ficheAncienne)).thenReturn(
                new FicheMesureResponseDTO(1L, 1L, "Nom", "Prenom", mesures, ficheAncienne.getDatePrise())
        );
        when(ficheMesureMapper.toDto(ficheRecente)).thenReturn(
                new FicheMesureResponseDTO(2L, 1L, "Nom", "Prenom", mesures, ficheRecente.getDatePrise())
        );

        // When
        Page<FicheMesureResponseDTO> result = ficheMesureService.getAllFiches(0, 10);

        // Then
        assertEquals(2, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).id()); // Ancienne en premier
        assertEquals(2L, result.getContent().get(1).id());
    }

    // ==================== TESTS POUR getFicheById ====================

    @Test
    void getFicheById_AvecIdExistant_DoitRetournerFiche() {
        // Given
        when(ficheMesureRepository.findById(1L)).thenReturn(Optional.of(ficheMesure));
        when(ficheMesureMapper.toDto(ficheMesure)).thenReturn(ficheMesureResponseDTO);

        // When
        FicheMesureResponseDTO result = ficheMesureService.getFicheById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Diallo", result.nomClient());

        verify(ficheMesureRepository, times(1)).findById(1L);
        verify(ficheMesureMapper, times(1)).toDto(ficheMesure);
    }

    @Test
    void getFicheById_AvecIdInexistant_DoitLancerException() {
        // Given
        when(ficheMesureRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            ficheMesureService.getFicheById(999L);
        });

        assertTrue(exception.getMessage().contains("Fiche de mesure introuvable"));
        assertTrue(exception.getMessage().contains("999"));

        verify(ficheMesureRepository, times(1)).findById(999L);
        verify(ficheMesureMapper, never()).toDto(any());
    }

    // ==================== TESTS D'INTÉGRATION ====================

    @Test
    void createAndGetFiche_DoitFonctionnerEnSequence() {
        // Given - Création
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(ficheMesureMapper.toEntity(ficheMesureRequestDTO, client)).thenReturn(ficheMesure);
        when(ficheMesureRepository.save(ficheMesure)).thenReturn(ficheMesure);
        when(ficheMesureMapper.toDto(ficheMesure)).thenReturn(ficheMesureResponseDTO);

        // When - Création
        FicheMesureResponseDTO created = ficheMesureService.createFicheMesure(ficheMesureRequestDTO);

        // Then - Vérification création
        assertNotNull(created);

        // Given - Récupération
        when(ficheMesureRepository.findById(1L)).thenReturn(Optional.of(ficheMesure));

        // When - Récupération
        FicheMesureResponseDTO retrieved = ficheMesureService.getFicheById(1L);

        // Then - Vérification récupération
        assertEquals(created.id(), retrieved.id());
    }

    @Test
    void createMultipleFichesForSameClient_DoitReussir() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.existsById(1L)).thenReturn(true);

        FicheMesure fiche1 = new FicheMesure();
        fiche1.setId(1L);
        fiche1.setClient(client);

        FicheMesure fiche2 = new FicheMesure();
        fiche2.setId(2L);
        fiche2.setClient(client);

        when(ficheMesureMapper.toEntity(any(), eq(client)))
                .thenReturn(fiche1)
                .thenReturn(fiche2);
        when(ficheMesureRepository.save(any()))
                .thenReturn(fiche1)
                .thenReturn(fiche2);
        when(ficheMesureMapper.toDto(any()))
                .thenReturn(new FicheMesureResponseDTO(1L, 1L, "Diallo", "Fatou", mesures, LocalDateTime.now()))
                .thenReturn(new FicheMesureResponseDTO(2L, 1L, "Diallo", "Fatou", mesures, LocalDateTime.now()));

        // When - Créer deux fiches
        FicheMesureResponseDTO fiche1Created = ficheMesureService.createFicheMesure(ficheMesureRequestDTO);
        FicheMesureResponseDTO fiche2Created = ficheMesureService.createFicheMesure(ficheMesureRequestDTO);

        // Then
        assertNotNull(fiche1Created);
        assertNotNull(fiche2Created);

        // When - Récupérer les fiches du client
        when(ficheMesureRepository.findByClientIdOrderByDatePriseDesc(1L))
                .thenReturn(List.of(fiche2, fiche1));
        when(ficheMesureMapper.toDto(fiche2)).thenReturn(
                new FicheMesureResponseDTO(2L, 1L, "Diallo", "Fatou", mesures, LocalDateTime.now())
        );
        when(ficheMesureMapper.toDto(fiche1)).thenReturn(
                new FicheMesureResponseDTO(1L, 1L, "Diallo", "Fatou", mesures, LocalDateTime.now())
        );

        List<FicheMesureResponseDTO> fiches = ficheMesureService.getFichesByClientId(1L);

        // Then
        assertEquals(2, fiches.size());
    }
}
