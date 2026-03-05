package com.inetum.rlibrarybackend.repository;

import com.inetum.rlibrarybackend.model.LoanRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRuleRepository extends JpaRepository<LoanRule, Integer> {
}

