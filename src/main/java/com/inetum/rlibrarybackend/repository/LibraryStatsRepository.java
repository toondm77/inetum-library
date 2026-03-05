package com.inetum.rlibrarybackend.repository;

import com.inetum.rlibrarybackend.model.LibraryStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryStatsRepository extends JpaRepository<LibraryStats, Integer> {
}

