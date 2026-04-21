package com.restaurant.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Restaurant Table Booking System
 * 
 * Architecture: Spring Boot MVC with Layered Architecture
 * - Controller Layer: Handles HTTP requests and responses (REST API)
 * - Service Layer: Contains business logic and validation
 * - Repository Layer: Data access using Spring Data JPA
 * - Model Layer: JPA Entities mapped to database tables
 * 
 * Design Patterns Used:
 * - MVC (Model-View-Controller)
 * - Repository Pattern (Data Access Abstraction)
 * - Service Layer Pattern (Business Logic Encapsulation)
 * - Dependency Injection (Spring IoC Container)
 * - Singleton Pattern (Spring Beans)
 * 
 * Database: MySQL with JPA/Hibernate ORM
 * Frontend: HTML5, CSS3, JavaScript (Single Page Application)
 */
@SpringBootApplication
public class BookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  Restaurant Table Booking System");
        System.out.println("  Running at: http://localhost:8080");
        System.out.println("========================================\n");
    }
}
