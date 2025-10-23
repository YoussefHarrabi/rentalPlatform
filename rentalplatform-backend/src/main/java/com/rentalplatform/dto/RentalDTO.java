package com.rentalplatform.dto;

import com.rentalplatform.model.RentalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private Long clientId;
    private String clientName;
    private String clientEmail;
    private Long ownerId;
    private String ownerName;
    private String ownerEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numberOfDays;
    private BigDecimal pricePerDay;
    private BigDecimal totalPrice;
    private RentalStatus status;
    private String clientMessage;
    private String ownerResponse;
    private LocalDateTime acceptedAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime completedAt;
    private Boolean equipmentReturned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}