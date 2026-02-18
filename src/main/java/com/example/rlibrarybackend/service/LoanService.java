package com.example.rlibrarybackend.service;

import com.example.rlibrarybackend.model.Loan;
import com.example.rlibrarybackend.repository.LoanRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
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

    public List<Loan> getAllLoans(int page, int size, String sort, String direction, String status, Long personId, LocalDate loanDateFrom, LocalDate loanDateTo) {
        log.debug("Fetching all loans page={} size={} sort={} direction={} status={} personId={} loanDateFrom={} loanDateTo={}", page, size, sort, direction, status, personId, loanDateFrom, loanDateTo);
        Specification<Loan> spec = Specification.where(null);
        if (status != null && !status.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("status")), status.toLowerCase()));
        }
        if (personId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("person").get("id"), personId.intValue()));
        }
        if (loanDateFrom != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("loanDate"), loanDateFrom));
        }
        if (loanDateTo != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("loanDate"), loanDateTo));
        }
        Sort sortOrder = Sort.by(Sort.Direction.fromString(direction != null ? direction : "asc"), sort != null && !sort.isBlank() ? sort : "loanDate");
        List<Loan> loans = loanRepository.findAll(spec, PageRequest.of(page, size, sortOrder)).getContent();
        log.debug("Fetched {} loans", loans.size());
        return loans;
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
