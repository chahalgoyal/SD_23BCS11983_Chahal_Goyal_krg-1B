package com.restaurant.booking.config;

import com.restaurant.booking.model.RestaurantTable;
import com.restaurant.booking.repository.TableRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration: DataInitializer
 * 
 * Seeds the database with sample restaurant tables on application startup.
 * Uses CommandLineRunner to execute after Spring context is initialized.
 * 
 * This follows the Database Seeding pattern for development/demo environments.
 * In production, this would be replaced with Flyway or Liquibase migrations.
 */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(TableRepository tableRepository) {
        return args -> {
            // Only seed if the database is empty
            if (tableRepository.count() == 0) {
                System.out.println("Seeding database with sample tables...");

                // Main Hall Tables (Tables 1-4)
                tableRepository.save(new RestaurantTable(1, 2, "Main Hall"));
                tableRepository.save(new RestaurantTable(2, 2, "Main Hall"));
                tableRepository.save(new RestaurantTable(3, 4, "Main Hall"));
                tableRepository.save(new RestaurantTable(4, 4, "Main Hall"));

                // Window Tables (Tables 5-7)
                tableRepository.save(new RestaurantTable(5, 2, "Window"));
                tableRepository.save(new RestaurantTable(6, 4, "Window"));
                tableRepository.save(new RestaurantTable(7, 6, "Window"));

                // Patio Tables (Tables 8-10)
                tableRepository.save(new RestaurantTable(8, 4, "Patio"));
                tableRepository.save(new RestaurantTable(9, 6, "Patio"));
                tableRepository.save(new RestaurantTable(10, 8, "Patio"));

                // Private Room Tables (Tables 11-12)
                tableRepository.save(new RestaurantTable(11, 8, "Private Room"));
                tableRepository.save(new RestaurantTable(12, 12, "Private Room"));

                System.out.println("Database seeded with 12 restaurant tables.");
            }
        };
    }
}
