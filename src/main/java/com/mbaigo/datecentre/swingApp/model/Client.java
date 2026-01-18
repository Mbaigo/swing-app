package com.mbaigo.datecentre.swingApp.model;

import com.mbaigo.datecentre.swingApp.enums.Genre;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Column(nullable = false)
    private String prenom;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Column(nullable = false, unique = true) // Unique pour éviter les doublons
    private String telephone;

    @Email(message = "Format email invalide")
    private String email;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Column(columnDefinition = "TEXT")
    private String notesMorphologie;

    @Column(updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }
}