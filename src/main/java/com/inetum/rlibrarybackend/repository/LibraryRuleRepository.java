package com.inetum.rlibrarybackend.repository;

import com.inetum.rlibrarybackend.model.LibraryRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryRuleRepository extends JpaRepository<LibraryRule, Integer> {
}

