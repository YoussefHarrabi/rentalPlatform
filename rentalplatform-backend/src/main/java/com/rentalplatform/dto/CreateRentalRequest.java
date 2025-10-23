package com.rentalplatform.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateRentalRequest {

    @NotNull(message = "Le produit est requis")
    private Long productId;

    @NotNull(message = "La date de début est requise")
    @FutureOrPresent(message = "La date de début doit être aujourd'hui ou dans le futur")
    private LocalDate startDate;

    @NotNull(message = "La date de fin est requise")
    private LocalDate endDate;

    private String clientMessage;
}