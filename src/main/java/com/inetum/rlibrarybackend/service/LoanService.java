package com.inetum.rlibrarybackend.service;

import com.inetum.rlibrarybackend.dto.LoanDto;
import com.inetum.rlibrarybackend.model.Book;
import com.inetum.rlibrarybackend.model.Loan;
import com.inetum.rlibrarybackend.model.LoanRule;
import com.inetum.rlibrarybackend.model.LoanStatus;
import com.inetum.rlibrarybackend.model.Person;
import com.inetum.rlibrarybackend.repository.BookRepository;
import com.inetum.rlibrarybackend.repository.LoanRepository;
import com.inetum.rlibrarybackend.repository.LoanRuleRepository;
import com.inetum.rlibrarybackend.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LoanService {
    private final LoanRepository loanRepository;
    private final PersonRepository personRepository;
    private final BookRepository bookRepository;
    private final LoanRuleRepository loanRuleRepository;

    public LoanService(LoanRepository loanRepository, PersonRepository personRepository,
                       BookRepository bookRepository, LoanRuleRepository loanRuleRepository) {
        this.loanRepository = loanRepository;
        this.personRepository = personRepository;
        this.bookRepository = bookRepository;
        this.loanRuleRepository = loanRuleRepository;
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
        Person person = personRepository.findById(dto.getPersonId().intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        List<Book> books = resolveBooks(dto.getBookIds());

        LoanRule loanRule = null;
        if (dto.getLoanRuleId() != null) {
            loanRule = loanRuleRepository.findById(dto.getLoanRuleId().intValue())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "LoanRule not found"));
        }

        Loan loan = new Loan();
        loan.setLoanDate(dto.getLoanDate());
        loan.setReturnDate(dto.getReturnDate());
        loan.setStatus(dto.getStatus() != null ? parseLoanStatus(dto.getStatus()) : LoanStatus.LOANED);
        loan.setPerson(person);
        loan.setLoanRule(loanRule);
        loan.setBooks(books);

        Loan saved = loanRepository.save(loan);
        log.info("Created loan id={}", saved.getId());
        return mapEntityToDto(saved);
    }

    public Optional<LoanDto> updateLoan(Long id, LoanDto dto) {
        Loan existing = loanRepository.findById(id.intValue()).orElse(null);
        if (existing == null) {
            log.warn("Update loan failed: id={} not found", id);
            return Optional.empty();
        }

        Person person = personRepository.findById(dto.getPersonId().intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        List<Book> books = resolveBooks(dto.getBookIds());

        LoanRule loanRule = existing.getLoanRule();
        if (dto.getLoanRuleId() != null) {
            loanRule = loanRuleRepository.findById(dto.getLoanRuleId().intValue())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "LoanRule not found"));
        }

        existing.setLoanDate(dto.getLoanDate());
        existing.setReturnDate(dto.getReturnDate());
        if (dto.getStatus() != null) {
            existing.setStatus(parseLoanStatus(dto.getStatus()));
        }
        existing.setPerson(person);
        existing.setLoanRule(loanRule);
        existing.setBooks(books);

        Loan saved = loanRepository.save(existing);
        log.info("Updated loan id={}", id);
        return Optional.of(mapEntityToDto(saved));
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

    private LoanStatus parseLoanStatus(String status) {
        try {
            return LoanStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid loan status '" + status + "'. Allowed values: " + java.util.Arrays.toString(LoanStatus.values()));
        }
    }

    private List<Book> resolveBooks(List<Long> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one book id is required");
        }
        return bookIds.stream()
                .map(bookId -> bookRepository.findById(bookId.intValue())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found: id=" + bookId)))
                .collect(Collectors.toCollection(java.util.ArrayList::new));
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
}
