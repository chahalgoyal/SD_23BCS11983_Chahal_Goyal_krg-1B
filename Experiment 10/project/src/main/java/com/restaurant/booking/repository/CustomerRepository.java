package com.restaurant.booking.repository;

import com.restaurant.booking.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository: CustomerRepository
 * 
 * Data Access Layer for Customer entity.
 * Extends JpaRepository which provides CRUD operations:
 *   - save(), findById(), findAll(), delete(), count(), etc.
 * 
 * Custom query methods use Spring Data JPA's method name query derivation.
 * Spring automatically generates SQL from the method name.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // SELECT * FROM customers WHERE email = ?
    Optional<Customer> findByEmail(String email);

    // SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM customers c WHERE c.email = ?
    boolean existsByEmail(String email);
}
