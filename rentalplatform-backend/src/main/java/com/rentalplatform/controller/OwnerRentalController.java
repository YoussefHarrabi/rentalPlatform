package com.rentalplatform.controller;

import com.rentalplatform.dto.MessageResponse;
import com.rentalplatform.dto.RentalDTO;
import com.rentalplatform.dto.RentalResponseRequest;
import com.rentalplatform.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owner/rentals")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
public class OwnerRentalController {

    private final RentalService rentalService;

    // Get all my rentals
    @GetMapping
    public ResponseEntity<List<RentalDTO>> getMyRentals(Authentication authentication) {
        String ownerEmail = authentication.getName();
        List<RentalDTO> rentals = rentalService.getMyRentals(ownerEmail);
        return ResponseEntity.ok(rentals);
    }

    // Get pending rentals
    @GetMapping("/pending")
    public ResponseEntity<List<RentalDTO>> getMyPendingRentals(Authentication authentication) {
        String ownerEmail = authentication.getName();
        List<RentalDTO> rentals = rentalService.getMyPendingRentals(ownerEmail);
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

    // Accept or reject rental
    @PostMapping("/{id}/respond")
    public ResponseEntity<RentalDTO> respondToRental(
            @PathVariable Long id,
            @Valid @RequestBody RentalResponseRequest request,
            Authentication authentication) {
        String ownerEmail = authentication.getName();
        RentalDTO rental = rentalService.respondToRental(id, request, ownerEmail);
        return ResponseEntity.ok(rental);
    }

    // Confirm equipment return
    @PatchMapping("/{id}/confirm-return")
    public ResponseEntity<RentalDTO> confirmEquipmentReturn(
            @PathVariable Long id,
            Authentication authentication) {
        String ownerEmail = authentication.getName();
        RentalDTO rental = rentalService.confirmEquipmentReturn(id, ownerEmail);
        return ResponseEntity.ok(rental);
    }
}