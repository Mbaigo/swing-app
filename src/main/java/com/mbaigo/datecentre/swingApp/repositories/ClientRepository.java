package com.mbaigo.datecentre.swingApp.repositories;

import com.mbaigo.datecentre.swingApp.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    // Méthode dérivée pour vérifier l'existence sans écrire de SQL
    boolean existsByTelephone(String telephone);

    // Recherche par nom (utile pour l'autocomplétion plus tard)
    Optional<Client> findByTelephone(String telephone);
}
