package com.mbaigo.datecentre.swingApp.repositories;

import com.mbaigo.datecentre.swingApp.model.FicheMesure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FicheMesureRepository extends JpaRepository<FicheMesure, Long> {

    // Récupérer tout l'historique d'un client, trié par date (le plus récent en premier)
    List<FicheMesure> findByClientIdOrderByDatePriseDesc(Long clientId);
}
