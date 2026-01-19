package com.mbaigo.datecentre.swingApp.controllers;

import com.mbaigo.datecentre.swingApp.dto.ArticleStockDto;
import com.mbaigo.datecentre.swingApp.services.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "Gestion des tissus et mercerie")
@CrossOrigin(origins = "http://localhost:5173")
public class StockController {

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<ArticleStockDto>> getStock() {
        return ResponseEntity.ok(stockService.getAllArticles());
    }

    @PostMapping
    public ResponseEntity<Long> ajouterArticle(@RequestBody @Valid ArticleStockDto dto) {
        Long id = stockService.ajouterArticle(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleStockDto> updateArticle(@PathVariable Long id, @RequestBody @Valid ArticleStockDto dto) {
        return ResponseEntity.ok(stockService.updateArticle(id, dto));
    }

    // Endpoint utile pour faire un réassort rapide (ex: "J'ai racheté 10m de tissu")
    @Operation(summary = "Ajout rapide de stock (Réassort)")
    @PatchMapping("/{id}/add")
    public ResponseEntity<Void> ajouterQuantite(@PathVariable Long id, @RequestParam Double qte) {
        stockService.crediterStock(id, qte);
        return ResponseEntity.ok().build();
    }
}
