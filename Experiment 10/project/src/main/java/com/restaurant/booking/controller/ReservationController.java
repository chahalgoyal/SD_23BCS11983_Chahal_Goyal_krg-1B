package com.restaurant.booking.controller;

import com.restaurant.booking.model.Reservation;
import com.restaurant.booking.model.ReservationStatus;
import com.restaurant.booking.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * REST Controller: ReservationController
 * 
 * Handles HTTP requests for reservation management.
 * 
 * API Endpoints:
 *   POST   /api/reservations              - Create a new reservation
 *   GET    /api/reservations              - Get all reservations
 *   GET    /api/reservations/{id}         - Get reservation by ID
 *   GET    /api/reservations/search       - Search by email
 *   GET    /api/reservations/date         - Get by date
 *   PUT    /api/reservations/{id}/cancel  - Cancel a reservation
 * 
 * Request/Response Format: JSON
 * HTTP Status Codes:
 *   200 - Success
 *   400 - Bad Request (validation error)
 *   404 - Not Found
 *   500 - Internal Server Error
 */
@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * POST /api/reservations
     * Creates a new table reservation.
     * 
     * Request Body (JSON):
     * {
     *   "customerName": "John Doe",
     *   "customerEmail": "john@example.com",
     *   "customerPhone": "9876543210",
     *   "tableId": 1,
     *   "reservationDate": "2024-12-25",
     *   "reservationTime": "19:00",
     *   "partySize": 4,
     *   "specialRequests": "Window seat preferred"
     * }
     */
    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody Map<String, Object> request) {
        try {
            String customerName = (String) request.get("customerName");
            String customerEmail = (String) request.get("customerEmail");
            String customerPhone = (String) request.get("customerPhone");
            Long tableId = Long.valueOf(request.get("tableId").toString());
            LocalDate date = LocalDate.parse((String) request.get("reservationDate"));
            LocalTime time = LocalTime.parse((String) request.get("reservationTime"));
            Integer partySize = Integer.valueOf(request.get("partySize").toString());
            String specialRequests = (String) request.getOrDefault("specialRequests", "");

            Reservation reservation = reservationService.makeReservation(
                customerName, customerEmail, customerPhone,
                tableId, date, time, partySize, specialRequests
            );

            return ResponseEntity.ok(reservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid request: " + e.getMessage()));
        }
    }

    /**
     * GET /api/reservations
     * Returns all reservations (sorted by date descending).
     */
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    /**
     * GET /api/reservations/{id}
     * Returns a specific reservation by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/reservations/search?email=john@example.com
     * Search reservations by customer email.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Reservation>> searchByEmail(@RequestParam String email) {
        List<Reservation> reservations = reservationService.getReservationsByEmail(email);
        return ResponseEntity.ok(reservations);
    }

    /**
     * GET /api/reservations/date?date=2024-12-25
     * Get reservations for a specific date.
     */
    @GetMapping("/date")
    public ResponseEntity<List<Reservation>> getByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reservationService.getReservationsByDate(date));
    }

    /**
     * PUT /api/reservations/{id}/cancel
     * Cancel an existing reservation.
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        try {
            Reservation cancelled = reservationService.cancelReservation(id);
            return ResponseEntity.ok(cancelled);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
