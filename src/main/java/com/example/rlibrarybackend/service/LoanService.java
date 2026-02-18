package com.example.rlibrarybackend.service;

import com.example.rlibrarybackend.model.Loan;
import com.example.rlibrarybackend.repository.LoanRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {
    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Optional<Loan> findLoanById(Long id) {
        return loanRepository.findById(id.intValue());
    }

    public Loan createLoan(Loan loan) {
        return loanRepository.save(loan);
    }

    public boolean deleteLoan(Integer id) {
        if (!loanRepository.existsById(id)) {
            return false;
        }
        loanRepository.deleteById(id);
        return true;
    }
}

