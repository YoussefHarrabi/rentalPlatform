package com.rentalplatform.service;

import com.rentalplatform.dto.CreateRentalRequest;
import com.rentalplatform.dto.RentalDTO;
import com.rentalplatform.dto.RentalResponseRequest;
import com.rentalplatform.model.*;
import com.rentalplatform.repository.ProductRepository;
import com.rentalplatform.repository.RentalRepository;
import com.rentalplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    // Client: Create rental request
    @Transactional
    public RentalDTO createRental(CreateRentalRequest request, String clientEmail) {
        User client = userRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        // Validations
        if (!product.getIsActive() || !product.getIsAvailable()) {
            throw new RuntimeException("Ce produit n'est pas disponible");
        }

        if (product.getOwner().getEmail().equals(clientEmail)) {
            throw new RuntimeException("Vous ne pouvez pas louer votre propre équipement");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("La date de fin doit être après la date de début");
        }

        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("La date de début doit être aujourd'hui ou dans le futur");
        }

        // Check if product is available for the requested dates
        List<RentalStatus> activeStatuses = Arrays.asList(
                RentalStatus.ACCEPTED,
                RentalStatus.ACTIVE
        );

        List<Rental> conflictingRentals = rentalRepository
                .findByProductAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        product,
                        activeStatuses,
                        request.getEndDate(),
                        request.getStartDate()
                );

        if (!conflictingRentals.isEmpty()) {
            throw new RuntimeException("Ce produit n'est pas disponible pour les dates sélectionnées");
        }

        // Calculate rental details
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        Rental rental = new Rental();
        rental.setProduct(product);
        rental.setClient(client);
        rental.setOwner(product.getOwner());
        rental.setStartDate(request.getStartDate());
        rental.setEndDate(request.getEndDate());
        rental.setNumberOfDays((int) days);
        rental.setPricePerDay(product.getPricePerDay());
        rental.setTotalPrice(product.getPricePerDay().multiply(java.math.BigDecimal.valueOf(days)));
        rental.setStatus(RentalStatus.PENDING);
        rental.setClientMessage(request.getClientMessage());
        rental.setEquipmentReturned(false);

        Rental savedRental = rentalRepository.save(rental);

        // Send email notification to owner
        emailService.sendRentalRequestToOwner(savedRental);

        return convertToDTO(savedRental);
    }

    // Client: Get my reservations
    public List<RentalDTO> getMyReservations(String clientEmail) {
        User client = userRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));

        return rentalRepository.findByClientOrderByCreatedAtDesc(client).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Client: Cancel reservation
    @Transactional
    public RentalDTO cancelReservation(Long rentalId, String clientEmail) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (!rental.getClient().getEmail().equals(clientEmail)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à annuler cette réservation");
        }

        if (rental.getStatus() != RentalStatus.PENDING) {
            throw new RuntimeException("Seules les réservations en attente peuvent être annulées");
        }

        rental.setStatus(RentalStatus.CANCELLED);
        Rental savedRental = rentalRepository.save(rental);

        return convertToDTO(savedRental);
    }

    // Owner: Get my rentals
    public List<RentalDTO> getMyRentals(String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("Propriétaire non trouvé"));

        return rentalRepository.findByOwnerOrderByCreatedAtDesc(owner).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Owner: Get pending rentals
    public List<RentalDTO> getMyPendingRentals(String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("Propriétaire non trouvé"));

        return rentalRepository.findByOwnerAndStatus(owner, RentalStatus.PENDING).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Owner: Respond to rental request (accept or reject)
    @Transactional
    public RentalDTO respondToRental(Long rentalId, RentalResponseRequest request, String ownerEmail) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (!rental.getOwner().getEmail().equals(ownerEmail)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à répondre à cette demande");
        }

        if (rental.getStatus() != RentalStatus.PENDING) {
            throw new RuntimeException("Cette demande a déjà été traitée");
        }

        rental.setOwnerResponse(request.getOwnerResponse());

        if (request.getAccepted()) {
            rental.setStatus(RentalStatus.ACCEPTED);
            rental.setAcceptedAt(LocalDateTime.now());

            // If start date is today, mark as active
            if (rental.getStartDate().equals(LocalDate.now())) {
                rental.setStatus(RentalStatus.ACTIVE);
                rental.getProduct().setIsAvailable(false);
                productRepository.save(rental.getProduct());
            }

            // Send acceptance email to client
            emailService.sendRentalAcceptedToClient(rental);
        } else {
            rental.setStatus(RentalStatus.REJECTED);
            rental.setRejectedAt(LocalDateTime.now());

            // Send rejection email to client
            emailService.sendRentalRejectedToClient(rental);
        }

        Rental savedRental = rentalRepository.save(rental);
        return convertToDTO(savedRental);
    }

    // Owner: Confirm equipment return
    @Transactional
    public RentalDTO confirmEquipmentReturn(Long rentalId, String ownerEmail) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (!rental.getOwner().getEmail().equals(ownerEmail)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à confirmer ce retour");
        }

        if (rental.getStatus() != RentalStatus.ACTIVE && rental.getStatus() != RentalStatus.ACCEPTED) {
            throw new RuntimeException("Cette location n'est pas active");
        }

        rental.setStatus(RentalStatus.COMPLETED);
        rental.setEquipmentReturned(true);
        rental.setCompletedAt(LocalDateTime.now());

        // Make product available again
        Product product = rental.getProduct();
        product.setIsAvailable(true);
        productRepository.save(product);

        Rental savedRental = rentalRepository.save(rental);
        return convertToDTO(savedRental);
    }

    // Admin: Get all rentals
    public List<RentalDTO> getAllRentals() {
        return rentalRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get rental by ID
    public RentalDTO getRentalById(Long id, String userEmail) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Check if user is authorized to view this rental
        if (!rental.getClient().equals(user) &&
                !rental.getOwner().equals(user) &&
                !user.getRoles().stream().anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à voir cette réservation");
        }

        return convertToDTO(rental);
    }

    // Automatic job to activate rentals (should be called daily)
    @Transactional
    public void activateRentalsForToday() {
        List<Rental> rentalsToActivate = rentalRepository.findByOwnerAndStatus(null, RentalStatus.ACCEPTED)
                .stream()
                .filter(rental -> rental.getStartDate().equals(LocalDate.now()))
                .collect(Collectors.toList());

        for (Rental rental : rentalsToActivate) {
            rental.setStatus(RentalStatus.ACTIVE);
            rental.getProduct().setIsAvailable(false);
            productRepository.save(rental.getProduct());
            rentalRepository.save(rental);
        }
    }

    // Convert Rental to RentalDTO
    private RentalDTO convertToDTO(Rental rental) {
        RentalDTO dto = new RentalDTO();
        dto.setId(rental.getId());
        dto.setProductId(rental.getProduct().getId());
        dto.setProductName(rental.getProduct().getName());
        dto.setProductImageUrl(rental.getProduct().getImageUrl());
        dto.setClientId(rental.getClient().getId());
        dto.setClientName(rental.getClient().getFullName());
        dto.setClientEmail(rental.getClient().getEmail());
        dto.setOwnerId(rental.getOwner().getId());
        dto.setOwnerName(rental.getOwner().getFullName());
        dto.setOwnerEmail(rental.getOwner().getEmail());
        dto.setStartDate(rental.getStartDate());
        dto.setEndDate(rental.getEndDate());
        dto.setNumberOfDays(rental.getNumberOfDays());
        dto.setPricePerDay(rental.getPricePerDay());
        dto.setTotalPrice(rental.getTotalPrice());
        dto.setStatus(rental.getStatus());
        dto.setClientMessage(rental.getClientMessage());
        dto.setOwnerResponse(rental.getOwnerResponse());
        dto.setAcceptedAt(rental.getAcceptedAt());
        dto.setRejectedAt(rental.getRejectedAt());
        dto.setCompletedAt(rental.getCompletedAt());
        dto.setEquipmentReturned(rental.getEquipmentReturned());
        dto.setCreatedAt(rental.getCreatedAt());
        dto.setUpdatedAt(rental.getUpdatedAt());
        return dto;
    }
}