
package com.aodb.repository;

import com.aodb.entity.AirportResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportResourceRepository extends JpaRepository<AirportResource, Long> {
    // Custom queries can be added here if needed
}