package com.mbaigo.datecentre.swingApp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. Récupère le chemin absolu proprement
        Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();

        // 2. Laisse Java générer l'URI officielle (gère parfaitement les // ou /// selon ton OS)
        // Résultat type : "file:///C:/Users/TonNom/.../uploads/"
        String uploadUri = uploadDir.toUri().toString();

        // 3. Application de la configuration
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadUri);
    }
}