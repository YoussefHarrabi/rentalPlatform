package com.rentalplatform.model;

public enum RentalStatus {
    PENDING,        // En attente de validation du propriétaire
    ACCEPTED,       // Acceptée par le propriétaire
    REJECTED,       // Refusée par le propriétaire
    ACTIVE,         // Location en cours
    COMPLETED,      // Terminée et équipement retourné
    CANCELLED       // Annulée par le client
}