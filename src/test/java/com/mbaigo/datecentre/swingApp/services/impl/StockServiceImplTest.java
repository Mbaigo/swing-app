package com.mbaigo.datecentre.swingApp.services.impl;

import com.mbaigo.datecentre.swingApp.dto.ArticleStockDto;
import com.mbaigo.datecentre.swingApp.models.next.ArticleStock;
import com.mbaigo.datecentre.swingApp.repositories.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    private ArticleStock article;
    private ArticleStockDto articleDto;

    @BeforeEach
    void setUp() {
        article = ArticleStock.builder()
                .id(1L)
                .nom("Tissu Coton")
                .reference("TC-001")
                .categorie("Tissus")
                .quantiteDisponible(100.0)
                .prixAchatUnitaire(15.50)
                .seuilAlerte(20.0)
                .fournisseur("Fournisseur A")
                .build();

        articleDto = new ArticleStockDto(
                1L,
                "Tissu Coton",
                "TC-001",
                "Tissus",
                100.0,
                15.50,
                20.0,
                "Fournisseur A",
                false
        );
    }

    // ==================== TESTS POUR getAllArticles ====================

    @Test
    void getAllArticles_AvecArticles_DoitRetournerListe() {
        // Given
        List<ArticleStock> articles = List.of(article);
        when(stockRepository.findAll()).thenReturn(articles);

        // When
        List<ArticleStockDto> result = stockService.getAllArticles();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Tissu Coton", result.get(0).nom());
        assertEquals("TC-001", result.get(0).reference());
        assertFalse(result.get(0).enAlerte());

        verify(stockRepository, times(1)).findAll();
    }

    @Test
    void getAllArticles_SansArticles_DoitRetournerListeVide() {
        // Given
        when(stockRepository.findAll()).thenReturn(new ArrayList<>());

        // When
        List<ArticleStockDto> result = stockService.getAllArticles();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(stockRepository, times(1)).findAll();
    }

    @Test
    void getAllArticles_AvecArticleEnAlerte_DoitMarquerEnAlerte() {
        // Given
        ArticleStock articleEnAlerte = ArticleStock.builder()
                .id(2L)
                .nom("Fil Rouge")
                .reference("FR-001")
                .categorie("Fils")
                .quantiteDisponible(10.0)
                .prixAchatUnitaire(5.0)
                .seuilAlerte(20.0)
                .fournisseur("Fournisseur B")
                .build();

        when(stockRepository.findAll()).thenReturn(List.of(articleEnAlerte));

        // When
        List<ArticleStockDto> result = stockService.getAllArticles();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).enAlerte(), "L'article devrait être en alerte");
    }

    // ==================== TESTS POUR ajouterArticle ====================

    @Test
    void ajouterArticle_AvecDonneesValides_DoitRetournerIdArticle() {
        // Given
        when(stockRepository.save(any(ArticleStock.class))).thenReturn(article);

        // When
        Long id = stockService.ajouterArticle(articleDto);

        // Then
        assertNotNull(id);
        assertEquals(1L, id);
        verify(stockRepository, times(1)).save(any(ArticleStock.class));
    }

    @Test
    void ajouterArticle_AvecSeuilAlerteNull_DoitReussir() {
        // Given
        ArticleStockDto dtoSansAlerte = new ArticleStockDto(
                null,
                "Bouton Blanc",
                "BB-001",
                "Accessoires",
                500.0,
                0.50,
                null,
                "Fournisseur C",
                false
        );

        ArticleStock articleSansAlerte = ArticleStock.builder()
                .id(3L)
                .nom("Bouton Blanc")
                .reference("BB-001")
                .categorie("Accessoires")
                .quantiteDisponible(500.0)
                .prixAchatUnitaire(0.50)
                .seuilAlerte(null)
                .fournisseur("Fournisseur C")
                .build();

        when(stockRepository.save(any(ArticleStock.class))).thenReturn(articleSansAlerte);

        // When
        Long id = stockService.ajouterArticle(dtoSansAlerte);

        // Then
        assertNotNull(id);
        assertEquals(3L, id);
    }

    // ==================== TESTS POUR updateArticle ====================

    @Test
    void updateArticle_AvecArticleExistant_DoitMettreAJour() {
        // Given
        ArticleStockDto updateDto = new ArticleStockDto(
                1L,
                "Tissu Coton Premium",
                "TC-001-PREMIUM",
                "Tissus",
                150.0,
                18.50,
                25.0,
                "Fournisseur A+",
                false
        );

        when(stockRepository.findById(1L)).thenReturn(Optional.of(article));
        when(stockRepository.save(any(ArticleStock.class))).thenReturn(article);

        // When
        ArticleStockDto result = stockService.updateArticle(1L, updateDto);

        // Then
        assertNotNull(result);
        verify(stockRepository, times(1)).findById(1L);
        verify(stockRepository, times(1)).save(article);
        
        // Vérifier que les valeurs ont été mises à jour
        assertEquals("Tissu Coton Premium", article.getNom());
        assertEquals("TC-001-PREMIUM", article.getReference());
        assertEquals(18.50, article.getPrixAchatUnitaire());
        assertEquals("Fournisseur A+", article.getFournisseur());
        assertEquals(150.0, article.getQuantiteDisponible());
    }

    @Test
    void updateArticle_AvecArticleInexistant_DoitLancerException() {
        // Given
        when(stockRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            stockService.updateArticle(999L, articleDto);
        });

        assertEquals("Article introuvable", exception.getMessage());
        verify(stockRepository, times(1)).findById(999L);
        verify(stockRepository, never()).save(any());
    }

    // ==================== TESTS POUR crediterStock ====================

    @Test
    void crediterStock_AvecQuantiteValide_DoitAugmenterStock() {
        // Given
        Double quantiteInitiale = article.getQuantiteDisponible();
        Double quantiteAjoutee = 50.0;
        
        when(stockRepository.findById(1L)).thenReturn(Optional.of(article));
        when(stockRepository.save(any(ArticleStock.class))).thenReturn(article);

        // When
        stockService.crediterStock(1L, quantiteAjoutee);

        // Then
        assertEquals(quantiteInitiale + quantiteAjoutee, article.getQuantiteDisponible());
        verify(stockRepository, times(1)).findById(1L);
        verify(stockRepository, times(1)).save(article);
    }

    @Test
    void crediterStock_AvecArticleInexistant_DoitLancerException() {
        // Given
        when(stockRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            stockService.crediterStock(999L, 50.0);
        });

        verify(stockRepository, times(1)).findById(999L);
        verify(stockRepository, never()).save(any());
    }

    @Test
    void crediterStock_AvecQuantiteNegative_DoitQuandMemeFonctionner() {
        // Given - Le service ne valide pas les quantités négatives
        when(stockRepository.findById(1L)).thenReturn(Optional.of(article));
        when(stockRepository.save(any(ArticleStock.class))).thenReturn(article);

        // When
        stockService.crediterStock(1L, -10.0);

        // Then
        assertEquals(90.0, article.getQuantiteDisponible());
    }

    // ==================== TESTS POUR debiterStock ====================

    @Test
    void debiterStock_AvecQuantiteSuffisante_DoitDiminuerStock() {
        // Given
        Double quantiteNecessaire = 30.0;
        when(stockRepository.findById(1L)).thenReturn(Optional.of(article));
        when(stockRepository.save(any(ArticleStock.class))).thenReturn(article);

        // When
        stockService.debiterStock(1L, quantiteNecessaire);

        // Then
        assertEquals(70.0, article.getQuantiteDisponible());
        verify(stockRepository, times(1)).findById(1L);
        verify(stockRepository, times(1)).save(article);
    }

    @Test
    void debiterStock_AvecQuantiteInsuffisante_DoitLancerException() {
        // Given
        Double quantiteNecessaire = 150.0; // Plus que disponible (100)
        when(stockRepository.findById(1L)).thenReturn(Optional.of(article));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            stockService.debiterStock(1L, quantiteNecessaire);
        });

        assertEquals("Stock insuffisant !", exception.getMessage());
        verify(stockRepository, times(1)).findById(1L);
        verify(stockRepository, never()).save(any());
        
        // Vérifier que la quantité n'a pas changé
        assertEquals(100.0, article.getQuantiteDisponible());
    }

    @Test
    void debiterStock_AvecArticleInexistant_DoitLancerException() {
        // Given
        when(stockRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            stockService.debiterStock(999L, 10.0);
        });

        verify(stockRepository, times(1)).findById(999L);
        verify(stockRepository, never()).save(any());
    }

    @Test
    void debiterStock_SousSeuilAlerte_DoitAfficherAlerte() {
        // Given
        article.setQuantiteDisponible(25.0); // Juste au-dessus du seuil
        article.setSeuilAlerte(20.0);
        
        Double quantiteNecessaire = 10.0; // Résultat: 15, sous le seuil
        
        when(stockRepository.findById(1L)).thenReturn(Optional.of(article));
        when(stockRepository.save(any(ArticleStock.class))).thenReturn(article);

        // When
        stockService.debiterStock(1L, quantiteNecessaire);

        // Then
        assertEquals(15.0, article.getQuantiteDisponible());
        assertTrue(article.getQuantiteDisponible() <= article.getSeuilAlerte());
        verify(stockRepository, times(1)).save(article);
    }

    @Test
    void debiterStock_AvecSeuilAlerteNull_DoitFonctionnerSansAlerte() {
        // Given
        article.setSeuilAlerte(null);
        when(stockRepository.findById(1L)).thenReturn(Optional.of(article));
        when(stockRepository.save(any(ArticleStock.class))).thenReturn(article);

        // When
        stockService.debiterStock(1L, 50.0);

        // Then
        assertEquals(50.0, article.getQuantiteDisponible());
        verify(stockRepository, times(1)).save(article);
    }

    @Test
    void debiterStock_QuantiteExacte_DoitLaisserStockAZero() {
        // Given
        article.setQuantiteDisponible(100.0);
        when(stockRepository.findById(1L)).thenReturn(Optional.of(article));
        when(stockRepository.save(any(ArticleStock.class))).thenReturn(article);

        // When
        stockService.debiterStock(1L, 100.0);

        // Then
        assertEquals(0.0, article.getQuantiteDisponible());
    }

    // ==================== TESTS POUR getArticlesEnAlerte ====================

    @Test
    void getArticlesEnAlerte_AvecArticlesEnAlerte_DoitRetournerListe() {
        // Given
        ArticleStock articleEnAlerte = ArticleStock.builder()
                .id(2L)
                .nom("Fil Rouge")
                .reference("FR-001")
                .categorie("Fils")
                .quantiteDisponible(10.0)
                .prixAchatUnitaire(5.0)
                .seuilAlerte(20.0)
                .fournisseur("Fournisseur B")
                .build();

        when(stockRepository.findArticlesEnAlerte()).thenReturn(List.of(articleEnAlerte));

        // When
        List<ArticleStockDto> result = stockService.getArticlesEnAlerte();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).enAlerte());
        assertEquals("Fil Rouge", result.get(0).nom());
        
        verify(stockRepository, times(1)).findArticlesEnAlerte();
    }

    @Test
    void getArticlesEnAlerte_SansArticlesEnAlerte_DoitRetournerListeVide() {
        // Given
        when(stockRepository.findArticlesEnAlerte()).thenReturn(new ArrayList<>());

        // When
        List<ArticleStockDto> result = stockService.getArticlesEnAlerte();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(stockRepository, times(1)).findArticlesEnAlerte();
    }

    @Test
    void getArticlesEnAlerte_AvecPlusieursArticles_DoitTousLesRetourner() {
        // Given
        ArticleStock article1 = ArticleStock.builder()
                .id(1L)
                .nom("Article 1")
                .reference("A1")
                .categorie("Cat1")
                .quantiteDisponible(5.0)
                .seuilAlerte(10.0)
                .prixAchatUnitaire(10.0)
                .fournisseur("F1")
                .build();

        ArticleStock article2 = ArticleStock.builder()
                .id(2L)
                .nom("Article 2")
                .reference("A2")
                .categorie("Cat2")
                .quantiteDisponible(3.0)
                .seuilAlerte(15.0)
                .prixAchatUnitaire(20.0)
                .fournisseur("F2")
                .build();

        when(stockRepository.findArticlesEnAlerte()).thenReturn(List.of(article1, article2));

        // When
        List<ArticleStockDto> result = stockService.getArticlesEnAlerte();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.get(0).enAlerte());
        assertTrue(result.get(1).enAlerte());
    }

    // ==================== TESTS POUR mapToDto (indirectement testés) ====================

    @Test
    void mapToDto_AvecSeuilAlerteEgal_DoitMarquerEnAlerte() {
        // Given
        article.setQuantiteDisponible(20.0);
        article.setSeuilAlerte(20.0);
        
        when(stockRepository.findAll()).thenReturn(List.of(article));

        // When
        List<ArticleStockDto> result = stockService.getAllArticles();

        // Then
        assertTrue(result.get(0).enAlerte(), "Quantité égale au seuil devrait être en alerte");
    }

    @Test
    void mapToDto_SansSeuilAlerte_DoitNePasMarquerEnAlerte() {
        // Given
        article.setQuantiteDisponible(1.0); // Très faible
        article.setSeuilAlerte(null); // Pas de seuil
        
        when(stockRepository.findAll()).thenReturn(List.of(article));

        // When
        List<ArticleStockDto> result = stockService.getAllArticles();

        // Then
        assertFalse(result.get(0).enAlerte(), "Sans seuil défini, ne devrait pas être en alerte");
    }

    @Test
    void mapToDto_AvecQuantiteSuperieure_DoitNePasMarquerEnAlerte() {
        // Given
        article.setQuantiteDisponible(100.0);
        article.setSeuilAlerte(20.0);
        
        when(stockRepository.findAll()).thenReturn(List.of(article));

        // When
        List<ArticleStockDto> result = stockService.getAllArticles();

        // Then
        assertFalse(result.get(0).enAlerte());
    }
}
