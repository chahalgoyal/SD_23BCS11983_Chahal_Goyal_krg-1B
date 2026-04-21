package com.restaurant.booking.repository;

import com.restaurant.booking.model.Reservation;
import com.restaurant.booking.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository: ReservationRepository
 * 
 * Data Access Layer for Reservation entity.
 * Provides methods for querying reservations by various criteria.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Find reservations by customer email
    @Query("SELECT r FROM Reservation r WHERE r.customer.email = :email ORDER BY r.reservationDate DESC, r.reservationTime DESC")
    List<Reservation> findByCustomerEmail(@Param("email") String email);

    // Find reservations by date
    List<Reservation> findByReservationDateOrderByReservationTimeAsc(LocalDate date);

    // Find reservations by status
    List<Reservation> findByStatus(ReservationStatus status);

    // Find reservations by date and status
    List<Reservation> findByReservationDateAndStatus(LocalDate date, ReservationStatus status);

    // Find reservations for a specific table on a specific date
    List<Reservation> findByTableIdAndReservationDateAndStatusIn(
        Long tableId, LocalDate date, List<ReservationStatus> statuses
    );

    // Count reservations by date
    long countByReservationDate(LocalDate date);

    // Find all reservations ordered by date
    @Query("SELECT r FROM Reservation r ORDER BY r.reservationDate DESC, r.reservationTime DESC")
    List<Reservation> findAllOrderByDateDesc();
}
