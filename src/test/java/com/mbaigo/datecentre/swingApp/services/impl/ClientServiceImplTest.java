package com.mbaigo.datecentre.swingApp.services.impl;

import com.mbaigo.datecentre.swingApp.dto.ClientRequestDTO;
import com.mbaigo.datecentre.swingApp.dto.ClientResponseDTO;
import com.mbaigo.datecentre.swingApp.dto.mappers.ClientMapper;
import com.mbaigo.datecentre.swingApp.models.Client;
import com.mbaigo.datecentre.swingApp.repositories.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client client;
    private ClientRequestDTO clientRequestDTO;
    private ClientResponseDTO clientResponseDTO;
    private String telephoneNormalise;

    @BeforeEach
    void setUp() {
        telephoneNormalise = "+221771234567";
        
        clientRequestDTO = new ClientRequestDTO(
                "Diallo",
                "Fatou",
                "77 123 45 67",
                "fatou.diallo@email.com",
                "Dakar, Senegal"
        );

        client = new Client();
        client.setId(1L);
        client.setNom("Diallo");
        client.setPrenom("Fatou");
        client.setTelephone(telephoneNormalise);
        client.setEmail("fatou.diallo@email.com");
        client.setAdresse("Dakar, Senegal");

        clientResponseDTO = new ClientResponseDTO(
                1L,
                "Diallo",
                "Fatou",
                telephoneNormalise,
                "fatou.diallo@email.com",
                "Dakar, Senegal"
        );
    }

    // ==================== TESTS POUR createClient ====================

    @Test
    void createClient_AvecDonneesValides_DoitCreerClient() {
        // Given
        when(clientRepository.existsByTelephone(telephoneNormalise)).thenReturn(false);
        when(clientMapper.toEntity(clientRequestDTO, telephoneNormalise)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.toDto(client)).thenReturn(clientResponseDTO);

        // When
        ClientResponseDTO result = clientService.createClient(clientRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals("Diallo", result.nom());
        assertEquals("Fatou", result.prenom());
        assertEquals(telephoneNormalise, result.telephone());

        verify(clientRepository, times(1)).existsByTelephone(telephoneNormalise);
        verify(clientMapper, times(1)).toEntity(clientRequestDTO, telephoneNormalise);
        verify(clientRepository, times(1)).save(client);
        verify(clientMapper, times(1)).toDto(client);
    }

    @Test
    void createClient_AvecTelephoneExistant_DoitLancerException() {
        // Given
        when(clientRepository.existsByTelephone(telephoneNormalise)).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clientService.createClient(clientRequestDTO);
        });

        assertTrue(exception.getMessage().contains("existe déjà"));
        verify(clientRepository, times(1)).existsByTelephone(telephoneNormalise);
        verify(clientRepository, never()).save(any());
    }

    @Test
    void createClient_AvecTelephoneNonNormalise_DoitNormaliser() {
        // Given
        ClientRequestDTO requestAvecEspaces = new ClientRequestDTO(
                "Diallo",
                "Fatou",
                "77-123-45-67",
                "fatou@email.com",
                "Dakar"
        );

        when(clientRepository.existsByTelephone(anyString())).thenReturn(false);
        when(clientMapper.toEntity(any(), anyString())).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.toDto(client)).thenReturn(clientResponseDTO);

        // When
        ClientResponseDTO result = clientService.createClient(requestAvecEspaces);

        // Then
        assertNotNull(result);
        verify(clientRepository, times(1)).existsByTelephone(anyString());
    }

    @Test
    void createClient_AvecEmailNull_DoitReussir() {
        // Given
        ClientRequestDTO requestSansEmail = new ClientRequestDTO(
                "Ndiaye",
                "Awa",
                "77 999 88 77",
                null,
                "Thies"
        );

        Client clientSansEmail = new Client();
        clientSansEmail.setNom("Ndiaye");
        clientSansEmail.setPrenom("Awa");
        clientSansEmail.setTelephone("+221779998877");
        clientSansEmail.setEmail(null);
        clientSansEmail.setAdresse("Thies");

        when(clientRepository.existsByTelephone(anyString())).thenReturn(false);
        when(clientMapper.toEntity(any(), anyString())).thenReturn(clientSansEmail);
        when(clientRepository.save(clientSansEmail)).thenReturn(clientSansEmail);
        when(clientMapper.toDto(clientSansEmail)).thenReturn(
                new ClientResponseDTO(2L, "Ndiaye", "Awa", "+221779998877", null, "Thies")
        );

        // When
        ClientResponseDTO result = clientService.createClient(requestSansEmail);

        // Then
        assertNotNull(result);
        assertNull(result.email());
    }

    // ==================== TESTS POUR getClientByTelephone ====================

    @Test
    void getClientByTelephone_AvecTelephoneExistant_DoitRetournerClient() {
        // Given
        when(clientRepository.findByTelephone(telephoneNormalise)).thenReturn(Optional.of(client));
        when(clientMapper.toDto(client)).thenReturn(clientResponseDTO);

        // When
        Optional<ClientResponseDTO> result = clientService.getClientByTelephone("77 123 45 67");

        // Then
        assertTrue(result.isPresent());
        assertEquals("Diallo", result.get().nom());
        assertEquals(telephoneNormalise, result.get().telephone());

        verify(clientRepository, times(1)).findByTelephone(telephoneNormalise);
        verify(clientMapper, times(1)).toDto(client);
    }

    @Test
    void getClientByTelephone_AvecTelephoneInexistant_DoitLancerException() {
        // Given
        String telephoneInconnu = "+221779999999";
        when(clientRepository.findByTelephone(telephoneInconnu)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            clientService.getClientByTelephone("77 999 99 99");
        });

        verify(clientRepository, times(1)).findByTelephone(anyString());
    }

    @Test
    void getClientByTelephone_AvecTelephoneNonNormalise_DoitNormaliserAvantRecherche() {
        // Given
        String telephoneAvecEspaces = "77-123-45-67";
        when(clientRepository.findByTelephone(anyString())).thenReturn(Optional.of(client));
        when(clientMapper.toDto(client)).thenReturn(clientResponseDTO);

        // When
        Optional<ClientResponseDTO> result = clientService.getClientByTelephone(telephoneAvecEspaces);

        // Then
        assertTrue(result.isPresent());
        verify(clientRepository, times(1)).findByTelephone(anyString());
    }

    // ==================== TESTS POUR updateClient ====================

    @Test
    void updateClient_AvecDonneesValides_DoitMettreAJour() {
        // Given
        ClientRequestDTO updateDto = new ClientRequestDTO(
                "Diallo",
                "Fatou Binta",
                "77 123 45 67",
                "fatou.nouvelle@email.com",
                "Dakar, Plateau"
        );

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.existsByTelephone(telephoneNormalise)).thenReturn(false);
        doNothing().when(clientMapper).updateEntityFromDto(client, updateDto, telephoneNormalise);
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.toDto(client)).thenReturn(clientResponseDTO);

        // When
        ClientResponseDTO result = clientService.updateClient(1L, updateDto);

        // Then
        assertNotNull(result);
        verify(clientRepository, times(1)).findById(1L);
        verify(clientMapper, times(1)).updateEntityFromDto(client, updateDto, telephoneNormalise);
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void updateClient_AvecClientInexistant_DoitLancerException() {
        // Given
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            clientService.updateClient(999L, clientRequestDTO);
        });

        assertTrue(exception.getMessage().contains("Client introuvable"));
        assertTrue(exception.getMessage().contains("999"));
        verify(clientRepository, times(1)).findById(999L);
        verify(clientRepository, never()).save(any());
    }

    @Test
    void updateClient_AvecNouveauTelephoneDejaUtilise_DoitLancerException() {
        // Given
        String nouveauTelephone = "+221770000000";
        ClientRequestDTO updateDto = new ClientRequestDTO(
                "Diallo",
                "Fatou",
                "77 000 00 00",
                "fatou@email.com",
                "Dakar"
        );

        client.setTelephone(telephoneNormalise); // Ancien téléphone

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.existsByTelephone(nouveauTelephone)).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clientService.updateClient(1L, updateDto);
        });

        assertTrue(exception.getMessage().contains("est déjà utilisé"));
        verify(clientRepository, times(1)).findById(1L);
        verify(clientRepository, never()).save(any());
    }

    @Test
    void updateClient_AvecMemeTelephone_DoitMettreAJourSansVerifierExistence() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        doNothing().when(clientMapper).updateEntityFromDto(client, clientRequestDTO, telephoneNormalise);
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.toDto(client)).thenReturn(clientResponseDTO);

        // When
        ClientResponseDTO result = clientService.updateClient(1L, clientRequestDTO);

        // Then
        assertNotNull(result);
        verify(clientRepository, times(1)).findById(1L);
        // Ne doit pas vérifier l'existence car c'est le même téléphone
        verify(clientRepository, never()).existsByTelephone(anyString());
        verify(clientRepository, times(1)).save(client);
    }

    // ==================== TESTS POUR getAllClients ====================

    @Test
    void getAllClients_AvecClients_DoitRetournerPageTriee() {
        // Given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
        
        List<Client> clients = List.of(client);
        Page<Client> clientPage = new PageImpl<>(clients, pageable, 1);

        when(clientRepository.findAll(pageable)).thenReturn(clientPage);
        when(clientMapper.toDto(client)).thenReturn(clientResponseDTO);

        // When
        Page<ClientResponseDTO> result = clientService.getAllClients(page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("Diallo", result.getContent().get(0).nom());

        verify(clientRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllClients_SansClients_DoitRetournerPageVide() {
        // Given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
        Page<Client> clientPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(clientRepository.findAll(pageable)).thenReturn(clientPage);

        // When
        Page<ClientResponseDTO> result = clientService.getAllClients(page, size);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void getAllClients_AvecPaginationDifferente_DoitUtiliserBonsParametres() {
        // Given
        int page = 2;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
        Page<Client> clientPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(clientRepository.findAll(pageable)).thenReturn(clientPage);

        // When
        Page<ClientResponseDTO> result = clientService.getAllClients(page, size);

        // Then
        assertNotNull(result);
        verify(clientRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllClients_DoitTrierParNomAscendant() {
        // Given
        Client client1 = new Client();
        client1.setNom("Aaaa");
        client1.setPrenom("First");

        Client client2 = new Client();
        client2.setNom("Zzzz");
        client2.setPrenom("Last");

        Pageable pageable = PageRequest.of(0, 10, Sort.by("nom").ascending());
        Page<Client> clientPage = new PageImpl<>(List.of(client1, client2), pageable, 2);

        when(clientRepository.findAll(pageable)).thenReturn(clientPage);
        when(clientMapper.toDto(client1)).thenReturn(
                new ClientResponseDTO(1L, "Aaaa", "First", "+221", null, null)
        );
        when(clientMapper.toDto(client2)).thenReturn(
                new ClientResponseDTO(2L, "Zzzz", "Last", "+221", null, null)
        );

        // When
        Page<ClientResponseDTO> result = clientService.getAllClients(0, 10);

        // Then
        assertEquals(2, result.getTotalElements());
        assertEquals("Aaaa", result.getContent().get(0).nom());
        assertEquals("Zzzz", result.getContent().get(1).nom());
    }

    // ==================== TESTS POUR getClientById ====================

    @Test
    void getClientById_AvecIdExistant_DoitRetournerClient() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientMapper.toDto(client)).thenReturn(clientResponseDTO);

        // When
        Optional<ClientResponseDTO> result = clientService.getClientById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().id());
        assertEquals("Diallo", result.get().nom());

        verify(clientRepository, times(1)).findById(1L);
        verify(clientMapper, times(1)).toDto(client);
    }

    @Test
    void getClientById_AvecIdInexistant_DoitRetournerOptionalVide() {
        // Given
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<ClientResponseDTO> result = clientService.getClientById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(clientRepository, times(1)).findById(999L);
        verify(clientMapper, never()).toDto(any());
    }

    @Test
    void getClientById_AvecIdNull_DoitGererCorrectement() {
        // Given
        when(clientRepository.findById(null)).thenReturn(Optional.empty());

        // When
        Optional<ClientResponseDTO> result = clientService.getClientById(null);

        // Then
        assertFalse(result.isPresent());
    }

    // ==================== TESTS D'INTÉGRATION ====================

    @Test
    void createAndGetClient_DoitFonctionnerEnSequence() {
        // Given - Création
        when(clientRepository.existsByTelephone(telephoneNormalise)).thenReturn(false);
        when(clientMapper.toEntity(clientRequestDTO, telephoneNormalise)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.toDto(client)).thenReturn(clientResponseDTO);

        // When - Création
        ClientResponseDTO created = clientService.createClient(clientRequestDTO);

        // Then - Vérification création
        assertNotNull(created);
        assertEquals(1L, created.id());

        // Given - Récupération par ID
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        // When - Récupération
        Optional<ClientResponseDTO> retrieved = clientService.getClientById(1L);

        // Then - Vérification récupération
        assertTrue(retrieved.isPresent());
        assertEquals(created.id(), retrieved.get().id());
    }

    @Test
    void updateClient_AvecTousLesChamps_DoitMettreAJourCompletement() {
        // Given
        ClientRequestDTO updateComplet = new ClientRequestDTO(
                "Nouveau Nom",
                "Nouveau Prénom",
                "77 999 88 77",
                "nouveau@email.com",
                "Nouvelle Adresse"
        );

        String nouveauTelNormalise = "+221779998877";

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.existsByTelephone(nouveauTelNormalise)).thenReturn(false);
        doNothing().when(clientMapper).updateEntityFromDto(client, updateComplet, nouveauTelNormalise);
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.toDto(client)).thenReturn(
                new ClientResponseDTO(1L, "Nouveau Nom", "Nouveau Prénom", 
                        nouveauTelNormalise, "nouveau@email.com", "Nouvelle Adresse")
        );

        // When
        ClientResponseDTO result = clientService.updateClient(1L, updateComplet);

        // Then
        assertNotNull(result);
        verify(clientMapper, times(1)).updateEntityFromDto(client, updateComplet, nouveauTelNormalise);
    }
}
