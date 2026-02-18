package com.example.rlibrarybackend.service;

import com.example.rlibrarybackend.model.Loan;
import com.example.rlibrarybackend.repository.LoanRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoanService {
    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public List<Loan> getAllLoans() {
        log.debug("Fetching all loans");
        List<Loan> loans = loanRepository.findAll();
        log.debug("Fetched {} loans", loans.size());
        return loans;
    }

    public Optional<Loan> findLoanById(Long id) {
        log.debug("Finding loan id={}", id);
        return loanRepository.findById(id.intValue());
    }

    public Loan createLoan(Loan loan) {
        Loan saved = loanRepository.save(loan);
        log.info("Created loan id={}", saved.getId());
        return saved;
    }

    public boolean deleteLoan(Integer id) {
        if (!loanRepository.existsById(id)) {
            log.warn("Loan id={} not found for delete", id);
            return false;
        }
        loanRepository.deleteById(id);
        log.info("Deleted loan id={}", id);
        return true;
    }
}
