package com.inetum.rlibrarybackend.service;

import com.inetum.rlibrarybackend.dto.LoanDto;
import com.inetum.rlibrarybackend.model.Book;
import com.inetum.rlibrarybackend.model.Loan;
import com.inetum.rlibrarybackend.model.LoanRule;
import com.inetum.rlibrarybackend.model.LoanStatus;
import com.inetum.rlibrarybackend.model.Person;
import com.inetum.rlibrarybackend.model.AccountStatus;
import com.inetum.rlibrarybackend.model.BookState;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("loanDate"), loanDateFrom.atStartOfDay()));
        }
        if (loanDateTo != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("loanDate"), loanDateTo.atTime(java.time.LocalTime.MAX)));
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

        validatePersonCanLoan(person);
        List<Book> books = resolveBooks(dto.getBookIds());
        validateBooksAreAvailableForLoan(books);

        books.forEach(book -> book.setBookState(BookState.BORROWED));

        LoanRule loanRule = null;
        if (dto.getLoanRuleId() != null) {
            loanRule = loanRuleRepository.findById(dto.getLoanRuleId().intValue())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "LoanRule not found"));
        }

        Loan loan = new Loan();
        loan.setLoanDate(dto.getLoanDate() != null ? dto.getLoanDate().toLocalDateTime() : null);
        loan.setReturnDate(dto.getReturnDate() != null ? dto.getReturnDate().toLocalDateTime() : null);
        loan.setStatus(dto.getStatus() != null ? parseLoanStatus(dto.getStatus()) : LoanStatus.LOANED);
        loan.setPerson(person);
        loan.setLoanRule(loanRule);
        loan.setBooks(books);

        Loan saved = loanRepository.save(loan);
        log.info("Created loan id={}", saved.getId());
        return mapEntityToDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<LoanDto> getLoansByBookId(Long bookId, int page, int size, String sort, String direction) {
        log.debug("Fetching loans for bookId={} page={} size={} sort={} direction={}", bookId, page, size, sort, direction);
        Sort sortOrder = Sort.by(Sort.Direction.fromString(direction != null ? direction : "desc"), sort != null && !sort.isBlank() ? sort : "loanDate");
        return loanRepository.findByBooks_Id(bookId.intValue(), PageRequest.of(page, size, sortOrder))
                .map(this::mapEntityToDto);
    }

    public Optional<LoanDto> updateLoan(Long id, LoanDto dto) {
        Loan existing = loanRepository.findById(id.intValue()).orElse(null);
        if (existing == null) {
            log.warn("Update loan failed: id={} not found", id);
            return Optional.empty();
        }

        Person person = personRepository.findById(dto.getPersonId().intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        validatePersonCanLoan(person);
        List<Book> books = resolveBooks(dto.getBookIds());

        LoanStatus requestedStatus = dto.getStatus() != null ? parseLoanStatus(dto.getStatus()) : existing.getStatus();

        if (requestedStatus == LoanStatus.RETURNED) {
            books.forEach(book -> {
                BookState currentState = book.getBookState();
                if (currentState == BookState.BORROWED || currentState == BookState.LOST) {
                    book.setBookState(BookState.AVAILABLE);
                }
            });
        }

        LoanRule loanRule = existing.getLoanRule();
        if (dto.getLoanRuleId() != null) {
            loanRule = loanRuleRepository.findById(dto.getLoanRuleId().intValue())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "LoanRule not found"));
        }

        existing.setLoanDate(dto.getLoanDate() != null ? dto.getLoanDate().toLocalDateTime() : null);
        existing.setReturnDate(dto.getReturnDate() != null
                ? dto.getReturnDate().toLocalDateTime()
                : (requestedStatus == LoanStatus.RETURNED ? LocalDateTime.now() : null));
        if (dto.getStatus() != null) {
            existing.setStatus(requestedStatus);
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
        for (int i = 0; i < bookIds.size(); i++) {
            for (int j = i + 1; j < bookIds.size(); j++) {
                if (bookIds.get(i).equals(bookIds.get(j))) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "A loan cannot contain the same book more than once. Duplicate book id: " + bookIds.get(i));
                }
            }
        }
        return bookIds.stream()
                .map(bookId -> bookRepository.findById(bookId.intValue())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found: id=" + bookId)))
                .collect(Collectors.toCollection(java.util.ArrayList::new));
    }

    private void validatePersonCanLoan(Person person) {
        if (person.getAccountStatus() == AccountStatus.BANNED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "person account is banned and cannot loan books");
        }
    }

    private void validateBooksAreAvailableForLoan(List<Book> books) {
        List<String> unavailableBooks = books.stream()
                .filter(book -> book.getBookState() != BookState.AVAILABLE)
                .map(Book::getTitle)
                .toList();

        if (!unavailableBooks.isEmpty()) {
            String message = "The following book(s) are not available for loan: ";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message + String.join(", ", unavailableBooks));
        }
    }

    private LoanDto mapEntityToDto(Loan loan) {
        LoanDto dto = new LoanDto();
        dto.setId(loan.getId() != null ? loan.getId().longValue() : null);
        dto.setLoanDate(loan.getLoanDate() != null ? loan.getLoanDate().atOffset(ZoneOffset.UTC) : null);
        dto.setReturnDate(loan.getReturnDate() != null ? loan.getReturnDate().atOffset(ZoneOffset.UTC) : null);
        dto.setStatus(loan.getStatus() != null ? loan.getStatus().name() : null);
        if (loan.getPerson() != null && loan.getPerson().getId() != null) {
            dto.setPersonId(loan.getPerson().getId().longValue());
            dto.setPersonName(loan.getPerson().getFirstName() + " " + loan.getPerson().getLastName());
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
