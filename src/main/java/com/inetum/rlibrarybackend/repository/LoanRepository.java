package com.inetum.rlibrarybackend.repository;

import com.inetum.rlibrarybackend.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Integer>, JpaSpecificationExecutor<Loan> {
    List<Loan> findByBooks_Id(Integer bookId);
    Page<Loan> findByBooks_Id(Integer bookId, Pageable pageable);
}
