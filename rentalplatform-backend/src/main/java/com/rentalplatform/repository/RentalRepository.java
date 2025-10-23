package com.rentalplatform.repository;

import com.rentalplatform.model.Rental;
import com.rentalplatform.model.RentalStatus;
import com.rentalplatform.model.User;
import com.rentalplatform.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    // Find by client
    List<Rental> findByClient(User client);
    List<Rental> findByClientOrderByCreatedAtDesc(User client);

    // Find by owner
    List<Rental> findByOwner(User owner);
    List<Rental> findByOwnerOrderByCreatedAtDesc(User owner);

    // Find by product
    List<Rental> findByProduct(Product product);

    // Find by status
    List<Rental> findByStatus(RentalStatus status);
    List<Rental> findByOwnerAndStatus(User owner, RentalStatus status);
    List<Rental> findByClientAndStatus(User client, RentalStatus status);

    // Check availability for date range
    List<Rental> findByProductAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Product product,
            List<RentalStatus> statuses,
            LocalDate endDate,
            LocalDate startDate
    );

    // Find active rentals that should be completed
    List<Rental> findByStatusAndEndDateBefore(RentalStatus status, LocalDate date);
}