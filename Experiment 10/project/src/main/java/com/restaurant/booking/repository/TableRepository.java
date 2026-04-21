package com.restaurant.booking.repository;

import com.restaurant.booking.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository: TableRepository
 * 
 * Data Access Layer for RestaurantTable entity.
 * Contains custom JPQL queries for finding available tables.
 */
@Repository
public interface TableRepository extends JpaRepository<RestaurantTable, Long> {

    // Find all active tables
    List<RestaurantTable> findByIsActiveTrue();

    // Find tables by location
    List<RestaurantTable> findByLocationAndIsActiveTrue(String location);

    // Find table by table number
    Optional<RestaurantTable> findByTableNumber(Integer tableNumber);

    // Find tables with minimum capacity
    List<RestaurantTable> findByCapacityGreaterThanEqualAndIsActiveTrue(Integer minCapacity);

    /**
     * Custom JPQL query: Find available tables for a specific date, time, and party size.
     * A table is available if:
     *   1. It is active
     *   2. Its capacity >= partySize
     *   3. It does NOT have a CONFIRMED/PENDING reservation overlapping the requested time
     *      (within a 2-hour window)
     */
    @Query("SELECT t FROM RestaurantTable t WHERE t.isActive = true " +
           "AND t.capacity >= :partySize " +
           "AND t.id NOT IN (" +
           "  SELECT r.table.id FROM Reservation r " +
           "  WHERE r.reservationDate = :date " +
           "  AND r.status IN ('CONFIRMED', 'PENDING') " +
           "  AND r.reservationTime BETWEEN :startTime AND :endTime" +
           ")")
    List<RestaurantTable> findAvailableTables(
        @Param("date") LocalDate date,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime,
        @Param("partySize") Integer partySize
    );
}
