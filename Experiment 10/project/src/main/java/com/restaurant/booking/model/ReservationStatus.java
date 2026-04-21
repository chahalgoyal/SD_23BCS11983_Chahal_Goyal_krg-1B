package com.restaurant.booking.model;

/**
 * Enum representing the lifecycle states of a reservation.
 * State transitions:
 *   PENDING -> CONFIRMED -> COMPLETED
 *   PENDING -> CANCELLED
 *   CONFIRMED -> CANCELLED
 */
public enum ReservationStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}
