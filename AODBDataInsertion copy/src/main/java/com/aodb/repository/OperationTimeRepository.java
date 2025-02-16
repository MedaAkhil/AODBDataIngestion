
package com.aodb.repository;

import com.aodb.entity.OperationTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationTimeRepository extends JpaRepository<OperationTime, Long> {
    // Custom queries can be added here if needed
}