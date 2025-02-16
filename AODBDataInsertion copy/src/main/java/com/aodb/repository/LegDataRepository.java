package com.aodb.repository;

import com.aodb.entity.LegData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LegDataRepository extends JpaRepository<LegData, Long> {
    // Custom queries can be added here if needed
}


