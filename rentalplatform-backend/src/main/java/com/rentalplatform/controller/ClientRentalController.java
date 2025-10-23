package com.rentalplatform.controller;

import com.rentalplatform.dto.CreateRentalRequest;
import com.rentalplatform.dto.MessageResponse;
import com.rentalplatform.dto.RentalDTO;
import com.rentalplatform.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client/rentals")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CLIENT', 'OWNER', 'ADMIN')")
public class ClientRentalController {

    private final RentalService rentalService;

    // Create rental request
    @PostMapping
    public ResponseEntity<RentalDTO> createRental(
            @Valid @RequestBody CreateRentalRequest request,
            Authentication authentication) {
        String clientEmail = authentication.getName();
        RentalDTO rental = rentalService.createRental(request, clientEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }

    // Get my reservations
    @GetMapping
    public ResponseEntity<List<RentalDTO>> getMyReservations(Authentication authentication) {
        String clientEmail = authentication.getName();
        List<RentalDTO> rentals = rentalService.getMyReservations(clientEmail);
        return ResponseEntity.ok(rentals);
    }

    // Get rental by ID
    @GetMapping("/{id}")
    public ResponseEntity<RentalDTO> getRentalById(
            @PathVariable Long id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        RentalDTO rental = rentalService.getRentalById(id, userEmail);
        return ResponseEntity.ok(rental);
    }

    // Cancel reservation
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<RentalDTO> cancelReservation(
            @PathVariable Long id,
            Authentication authentication) {
        String clientEmail = authentication.getName();
        RentalDTO rental = rentalService.cancelReservation(id, clientEmail);
        return ResponseEntity.ok(rental);
    }
}