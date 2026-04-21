package com.restaurant.booking.service;

import com.restaurant.booking.model.RestaurantTable;
import com.restaurant.booking.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Service: TableService
 * 
 * Business Logic Layer for restaurant table management.
 * 
 * Responsibilities:
 *   - CRUD operations for tables
 *   - Availability checking with time-slot collision detection
 *   - Table filtering by capacity and location
 * 
 * Design Pattern: Service Layer Pattern
 *   Acts as a facade between the Controller and Repository layers,
 *   encapsulating business rules and transaction management.
 */
@Service
@Transactional
public class TableService {

    private final TableRepository tableRepository;

    @Autowired
    public TableService(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    // ========== READ OPERATIONS ==========

    public List<RestaurantTable> getAllTables() {
        return tableRepository.findAll();
    }

    public List<RestaurantTable> getActiveTables() {
        return tableRepository.findByIsActiveTrue();
    }

    public Optional<RestaurantTable> getTableById(Long id) {
        return tableRepository.findById(id);
    }

    public Optional<RestaurantTable> getTableByNumber(Integer tableNumber) {
        return tableRepository.findByTableNumber(tableNumber);
    }

    /**
     * Find tables available for a given date, time, and party size.
     * Uses a 2-hour reservation window to detect conflicts.
     * 
     * Algorithm:
     *   1. Calculate the time window (requested time ± 1.5 hours)
     *   2. Query tables that are active, have sufficient capacity,
     *      and don't have conflicting reservations
     *   3. Return matching tables sorted by capacity (best fit)
     */
    public List<RestaurantTable> getAvailableTables(LocalDate date, LocalTime time, Integer partySize) {
        // Define a 2-hour reservation window
        LocalTime startTime = time.minusHours(1).minusMinutes(30);
        LocalTime endTime = time.plusHours(1).plusMinutes(30);
        
        return tableRepository.findAvailableTables(date, startTime, endTime, partySize);
    }

    // ========== WRITE OPERATIONS ==========

    public RestaurantTable createTable(RestaurantTable table) {
        // Validate unique table number
        if (tableRepository.findByTableNumber(table.getTableNumber()).isPresent()) {
            throw new IllegalArgumentException("Table number " + table.getTableNumber() + " already exists");
        }
        return tableRepository.save(table);
    }

    public RestaurantTable updateTable(Long id, RestaurantTable updatedTable) {
        RestaurantTable existing = tableRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Table not found with id: " + id));
        
        existing.setCapacity(updatedTable.getCapacity());
        existing.setLocation(updatedTable.getLocation());
        existing.setActive(updatedTable.isActive());
        
        return tableRepository.save(existing);
    }

    public void toggleTableStatus(Long id) {
        RestaurantTable table = tableRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Table not found with id: " + id));
        table.setActive(!table.isActive());
        tableRepository.save(table);
    }
}
