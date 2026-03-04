package com.inetum.rlibrarybackend.repository;

import com.inetum.rlibrarybackend.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Integer>, JpaSpecificationExecutor<Loan> {
}
