package com.restaurant.booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Entity: RestaurantTable
 * 
 * Represents a physical table in the restaurant.
 * Mapped to 'restaurant_tables' table in the database.
 * 
 * Attributes:
 *   - tableNumber: Unique identifier visible to staff/customers
 *   - capacity: Maximum number of guests the table can seat
 *   - location: Physical location within the restaurant
 *   - isActive: Whether the table is currently in service
 * 
 * Relationships:
 *   RestaurantTable (1) ----< (Many) Reservation
 */
@Entity
@Table(name = "restaurant_tables")
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Table number is required")
    @Min(value = 1, message = "Table number must be positive")
    @Column(name = "table_number", nullable = false, unique = true)
    private Integer tableNumber;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 20, message = "Capacity cannot exceed 20")
    @Column(nullable = false)
    private Integer capacity;

    @NotBlank(message = "Location is required")
    @Column(nullable = false, length = 50)
    private String location; // e.g., "Window", "Patio", "Main Hall", "Private Room"

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // Default constructor
    public RestaurantTable() {}

    // Parameterized constructor
    public RestaurantTable(Integer tableNumber, Integer capacity, String location) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.location = location;
        this.isActive = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getTableNumber() { return tableNumber; }
    public void setTableNumber(Integer tableNumber) { this.tableNumber = tableNumber; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return "RestaurantTable{id=" + id + ", tableNumber=" + tableNumber + 
               ", capacity=" + capacity + ", location='" + location + "'}";
    }
}
