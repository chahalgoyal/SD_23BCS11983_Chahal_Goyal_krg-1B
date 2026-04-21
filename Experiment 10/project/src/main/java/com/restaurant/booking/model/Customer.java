package com.restaurant.booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Entity: Customer
 * 
 * Represents a customer who can make table reservations.
 * Mapped to 'customers' table in the database.
 * 
 * Relationships:
 *   Customer (1) ----< (Many) Reservation
 * 
 * Database Normalization: This entity is in 3NF (Third Normal Form)
 * - All attributes depend on the primary key (1NF)
 * - No partial dependencies (2NF)
 * - No transitive dependencies (3NF)
 */
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Please provide a valid phone number")
    @Column(nullable = false, length = 20)
    private String phone;

    // Default constructor (required by JPA)
    public Customer() {}

    // Parameterized constructor
    public Customer(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return "Customer{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}
