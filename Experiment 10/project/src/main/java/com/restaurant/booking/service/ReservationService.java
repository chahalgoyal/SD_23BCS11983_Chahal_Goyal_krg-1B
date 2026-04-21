package com.restaurant.booking.service;

import com.restaurant.booking.model.*;
import com.restaurant.booking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Service: ReservationService
 * 
 * Core Business Logic Layer for the reservation system.
 * 
 * Responsibilities:
 *   - Making new reservations with validation
 *   - Cancelling existing reservations
 *   - Checking table availability (conflict detection)
 *   - Retrieving reservations by various criteria
 * 
 * Business Rules:
 *   1. A table cannot have overlapping reservations (within 2-hour window)
 *   2. Reservation date must be today or in the future
 *   3. Party size must not exceed table capacity
 *   4. Only CONFIRMED/PENDING reservations can be cancelled
 * 
 * Transaction Management:
 *   All write operations are wrapped in @Transactional to ensure
 *   ACID properties (Atomicity, Consistency, Isolation, Durability)
 */
@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final TableRepository tableRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository,
                              CustomerRepository customerRepository,
                              TableRepository tableRepository) {
        this.reservationRepository = reservationRepository;
        this.customerRepository = customerRepository;
        this.tableRepository = tableRepository;
    }

    // ========== CORE BOOKING LOGIC ==========

    /**
     * Make a new reservation.
     * 
     * Algorithm:
     *   1. Find or create customer by email
     *   2. Validate table exists and is active
     *   3. Validate party size fits table capacity
     *   4. Check for time-slot conflicts
     *   5. Create and persist the reservation
     *   6. Return the confirmed reservation
     * 
     * Time Complexity: O(n) where n = number of existing reservations for the table on that date
     * Space Complexity: O(1)
     */
    public Reservation makeReservation(String customerName, String customerEmail, String customerPhone,
                                        Long tableId, LocalDate date, LocalTime time,
                                        Integer partySize, String specialRequests) {
        
        // Step 1: Find or create customer
        Customer customer = customerRepository.findByEmail(customerEmail)
            .map(existing -> {
                existing.setName(customerName);
                existing.setPhone(customerPhone);
                return customerRepository.save(existing);
            })
            .orElseGet(() -> customerRepository.save(new Customer(customerName, customerEmail, customerPhone)));

        // Step 2: Validate table
        RestaurantTable table = tableRepository.findById(tableId)
            .orElseThrow(() -> new IllegalArgumentException("Table not found with id: " + tableId));

        if (!table.isActive()) {
            throw new IllegalArgumentException("Table " + table.getTableNumber() + " is currently not available");
        }

        // Step 3: Validate party size
        if (partySize > table.getCapacity()) {
            throw new IllegalArgumentException(
                "Party size (" + partySize + ") exceeds table capacity (" + table.getCapacity() + ")");
        }

        // Step 4: Check for time-slot conflicts (2-hour reservation window)
        LocalTime windowStart = time.minusHours(1).minusMinutes(30);
        LocalTime windowEnd = time.plusHours(1).plusMinutes(30);
        
        List<Reservation> conflicts = reservationRepository
            .findByTableIdAndReservationDateAndStatusIn(
                tableId, date, List.of(ReservationStatus.CONFIRMED, ReservationStatus.PENDING));

        boolean hasConflict = conflicts.stream()
            .anyMatch(r -> !r.getReservationTime().isBefore(windowStart) && 
                          !r.getReservationTime().isAfter(windowEnd));

        if (hasConflict) {
            throw new IllegalArgumentException(
                "Table " + table.getTableNumber() + " is already booked for this time slot");
        }

        // Step 5: Create reservation
        Reservation reservation = new Reservation(customer, table, date, time, partySize, specialRequests);
        
        // Step 6: Persist and return
        return reservationRepository.save(reservation);
    }

    // ========== CANCELLATION ==========

    /**
     * Cancel an existing reservation.
     * Only CONFIRMED or PENDING reservations can be cancelled.
     */
    public Reservation cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found with id: " + reservationId));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalArgumentException("Reservation is already cancelled");
        }

        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot cancel a completed reservation");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        return reservationRepository.save(reservation);
    }

    // ========== READ OPERATIONS ==========

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAllOrderByDateDesc();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> getReservationsByEmail(String email) {
        return reservationRepository.findByCustomerEmail(email);
    }

    public List<Reservation> getReservationsByDate(LocalDate date) {
        return reservationRepository.findByReservationDateOrderByReservationTimeAsc(date);
    }

    public List<Reservation> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }

    // ========== STATISTICS ==========

    public long getTotalReservationsForDate(LocalDate date) {
        return reservationRepository.countByReservationDate(date);
    }
}
