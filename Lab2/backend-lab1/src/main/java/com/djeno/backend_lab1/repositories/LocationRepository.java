package com.djeno.backend_lab1.repositories;

import com.djeno.backend_lab1.models.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    boolean existsByIdAndUserId(Long id, Long userId);
}
