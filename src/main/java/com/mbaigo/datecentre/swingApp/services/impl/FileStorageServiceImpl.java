package com.mbaigo.datecentre.swingApp.services.impl;


import com.mbaigo.datecentre.swingApp.services.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    // Dossier où seront stockées les images physiquement (à la racine de ton projet par défaut)
    private final Path fileStorageLocation;

    public FileStorageServiceImpl() {
        // Crée le chemin du dossier "uploads"
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

        try {
            // Crée le dossier s'il n'existe pas encore
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Impossible de créer le répertoire où les fichiers téléchargés seront stockés.", ex);
        }
    }
    @Override
    public List<String> saveFiles(List<MultipartFile> files) {
        List<String> savedFileUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            // Nettoyage du nom de fichier original
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

            try {
                // Vérification de sécurité de base
                if (originalFileName.contains("..")) {
                    throw new RuntimeException("Désolé! Le nom du fichier contient une séquence de chemin invalide " + originalFileName);
                }

                // Génération d'un nom unique avec UUID
                String fileExtension = "";
                int lastIndex = originalFileName.lastIndexOf('.');
                if (lastIndex > 0) {
                    fileExtension = originalFileName.substring(lastIndex);
                }
                String newFileName = UUID.randomUUID().toString() + fileExtension;

                // Emplacement cible
                Path targetLocation = this.fileStorageLocation.resolve(newFileName);

                // Copie du fichier (remplace s'il existe déjà)
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                // URL virtuelle que le Frontend utilisera (nécessite une config de ressource, voir étape 3)
                String fileDownloadUri = "/uploads/" + newFileName;
                savedFileUrls.add(fileDownloadUri);

            } catch (IOException ex) {
                throw new RuntimeException("Impossible de stocker le fichier " + originalFileName + ". Veuillez réessayer!", ex);
            }
        }
        return savedFileUrls;
    }
}
