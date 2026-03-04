package com.example.rlibrarybackend.service;

import com.example.rlibrarybackend.dto.LoanDto;
import com.example.rlibrarybackend.model.Loan;
import com.example.rlibrarybackend.repository.LoanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoanService {
    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public Page<LoanDto> getAllLoans(int page, int size, String sort, String direction, String status, Long personId, LocalDate loanDateFrom, LocalDate loanDateTo) {
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
        Page<LoanDto> loans = loanRepository.findAll(spec, PageRequest.of(page, size, sortOrder)).map(this::mapEntityToDto);
        log.debug("Fetched {} loans", loans.getContent().size());
        return loans;
    }

    public Optional<LoanDto> findLoanById(Long id) {
        log.debug("Finding loan id={}", id);
        return loanRepository.findById(id.intValue()).map(this::mapEntityToDto);
    }

    public LoanDto createLoan(LoanDto dto) {
        Loan saved = loanRepository.save(mapDtoToEntity(dto));
        log.info("Created loan id={}", saved.getId());
        return mapEntityToDto(saved);
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

    private LoanDto mapEntityToDto(Loan loan) {
        LoanDto dto = new LoanDto();
        dto.setId(loan.getId() != null ? loan.getId().longValue() : null);
        dto.setLoanDate(loan.getLoanDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setStatus(loan.getStatus() != null ? loan.getStatus().name() : null);
        if (loan.getPerson() != null && loan.getPerson().getId() != null) {
            dto.setPersonId(loan.getPerson().getId().longValue());
        }
        if (loan.getLoanRule() != null && loan.getLoanRule().getId() != null) {
            dto.setLoanRuleId(loan.getLoanRule().getId().longValue());
        }
        if (loan.getBooks() != null) {
            dto.setBookIds(loan.getBooks().stream()
                    .filter(book -> book.getId() != null)
                    .map(book -> book.getId().longValue())
                    .toList());
        }
        return dto;
    }

    private Loan mapDtoToEntity(LoanDto dto) {
        Loan loan = new Loan();
        loan.setLoanDate(dto.getLoanDate());
        loan.setReturnDate(dto.getReturnDate());
        return loan;
    }
}
