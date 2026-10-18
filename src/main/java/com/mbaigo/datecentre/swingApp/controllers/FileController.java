package com.mbaigo.datecentre.swingApp.controllers;

import com.mbaigo.datecentre.swingApp.services.impl.FileStorageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fichiers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class FileController {

    private final FileStorageServiceImpl fileStorageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<String>> uploadFiles(@RequestParam("fichiers") List<MultipartFile> fichiers) {

        // On délègue la sauvegarde physique au service
        List<String> fileUrls = fileStorageService.saveFiles(fichiers);

        // On retourne la liste des chemins (ex: ["/uploads/img_123.jpg", "/uploads/img_456.png"])
        return ResponseEntity.ok(fileUrls);
    }
}
