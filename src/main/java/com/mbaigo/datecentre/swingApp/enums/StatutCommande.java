package com.mbaigo.datecentre.swingApp.enums;

public enum StatutCommande {
    EN_ATTENTE,      // Devis accepté, en attente de démarrage
    EN_COURS,        // Coupe ou Couture commencée
    ESSAYAGE,        // Prêt pour essayage client
    A_REPRENDRE,     // Retouches nécessaires après essayage
    TERMINEE,        // Prêt à livrer
    LIVREE,          // Client parti avec
    ANNULEE
}
