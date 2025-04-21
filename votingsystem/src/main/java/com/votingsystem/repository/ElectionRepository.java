package com.votingsystem.repository;

import com.votingsystem.model.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {

    List<Election> findByActiveTrue();

    @Query("SELECT e FROM Election e WHERE e.startDate <= ?1 AND e.endDate >= ?1 AND e.active = true")
    List<Election> findCurrentlyActiveElections(LocalDateTime now);

    @Query("SELECT e FROM Election e WHERE e.endDate < ?1")
    List<Election> findPastElections(LocalDateTime now);
}