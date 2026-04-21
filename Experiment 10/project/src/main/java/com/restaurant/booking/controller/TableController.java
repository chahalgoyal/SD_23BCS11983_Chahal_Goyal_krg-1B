package com.restaurant.booking.controller;

import com.restaurant.booking.model.RestaurantTable;
import com.restaurant.booking.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * REST Controller: TableController
 * 
 * Handles HTTP requests for table management.
 * 
 * API Endpoints:
 *   GET    /api/tables                - Get all tables
 *   GET    /api/tables/available      - Get available tables (with filters)
 *   GET    /api/tables/{id}           - Get table by ID
 *   POST   /api/tables                - Create a new table (Admin)
 *   PUT    /api/tables/{id}           - Update a table (Admin)
 *   PATCH  /api/tables/{id}/toggle    - Toggle table active status (Admin)
 */
@RestController
@RequestMapping("/api/tables")
@CrossOrigin(origins = "*")
public class TableController {

    private final TableService tableService;

    @Autowired
    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    /**
     * GET /api/tables
     * Returns all restaurant tables.
     */
    @GetMapping
    public ResponseEntity<List<RestaurantTable>> getAllTables() {
        return ResponseEntity.ok(tableService.getActiveTables());
    }

    /**
     * GET /api/tables/all
     * Returns all tables including inactive ones (Admin).
     */
    @GetMapping("/all")
    public ResponseEntity<List<RestaurantTable>> getAllTablesAdmin() {
        return ResponseEntity.ok(tableService.getAllTables());
    }

    /**
     * GET /api/tables/available?date=2024-12-25&time=19:00&partySize=4
     * Returns tables available for a specific date, time, and party size.
     */
    @GetMapping("/available")
    public ResponseEntity<List<RestaurantTable>> getAvailableTables(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            @RequestParam Integer partySize) {
        
        List<RestaurantTable> available = tableService.getAvailableTables(date, time, partySize);
        return ResponseEntity.ok(available);
    }

    /**
     * GET /api/tables/{id}
     * Returns a specific table by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantTable> getTableById(@PathVariable Long id) {
        return tableService.getTableById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/tables
     * Creates a new restaurant table.
     */
    @PostMapping
    public ResponseEntity<?> createTable(@RequestBody RestaurantTable table) {
        try {
            RestaurantTable created = tableService.createTable(table);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/tables/{id}
     * Updates an existing table.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTable(@PathVariable Long id, @RequestBody RestaurantTable table) {
        try {
            RestaurantTable updated = tableService.updateTable(id, table);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PATCH /api/tables/{id}/toggle
     * Toggles a table's active status.
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleTableStatus(@PathVariable Long id) {
        try {
            tableService.toggleTableStatus(id);
            return ResponseEntity.ok(Map.of("message", "Table status toggled successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
