package com.rentalplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RentalResponseRequest {

    @NotNull(message = "La décision est requise")
    private Boolean accepted;

    private String ownerResponse;
}