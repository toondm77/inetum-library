package com.example.rlibrarybackend.controller;

import com.example.rlibrarybackend.api.LoansApi;
import com.example.rlibrarybackend.dto.LoanDto;
import com.example.rlibrarybackend.dto.PagedLoanResponse;
import com.example.rlibrarybackend.model.Loan;
import com.example.rlibrarybackend.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class LoanController implements LoansApi {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @Override
    @PreAuthorize("hasAnyRole('WERKNEMER', 'VERANTWOORDELIJKE')")
    public ResponseEntity<PagedLoanResponse> loansGet(Integer page, Integer size, String sort, String direction, String status, Long personId, LocalDate loanDateFrom, LocalDate loanDateTo) {
        int p = page != null ? page : 0;
        int s = size != null ? size : 20;
        String sortField = (sort != null && !sort.isBlank()) ? sort : "loanDate";
        String dir = (direction != null && !direction.isBlank()) ? direction : "asc";
        log.info("GET /loans requested page={} size={} sort={} direction={}", p, s, sortField, dir);
        Page<Loan> loansPage = loanService.getAllLoans(p, s, sortField, dir, status, personId, loanDateFrom, loanDateTo);
        List<LoanDto> items = loansPage.getContent().stream()
                .map(this::mapEntityToDto)
                .toList();
        PagedLoanResponse response = new PagedLoanResponse(
                loansPage.getNumber(),
                loansPage.getSize(),
                loansPage.getTotalElements(),
                loansPage.getTotalPages(),
                items
        );
        log.debug("GET /loans returned {} items", items.size());
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasAnyRole('WERKNEMER', 'VERANTWOORDELIJKE')")
    public ResponseEntity<LoanDto> loansIdGet(Long id) {
        log.info("GET /loans/{} requested", id);
        return loanService.findLoanById(id)
                .map(loan -> {
                    log.debug("GET /loans/{} found", id);
                    return ResponseEntity.ok(mapEntityToDto(loan));
                })
                .orElseGet(() -> {
                    log.warn("GET /loans/{} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Override
    @PreAuthorize("hasRole('VERANTWOORDELIJKE')")
    public ResponseEntity<LoanDto> loansPost(LoanDto loanDto) {
        log.info("POST /loans requested");
        Loan created = loanService.createLoan(mapDtoToEntity(loanDto));
        log.info("POST /loans created id={}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapEntityToDto(created));
    }

    @Override
    @PreAuthorize("hasRole('VERANTWOORDELIJKE')")
    public ResponseEntity<Void> loansIdDelete(Long id) {
        log.info("DELETE /loans/{} requested", id);
        boolean deleted = loanService.deleteLoan(id.intValue());
        if (!deleted) {
            log.warn("DELETE /loans/{} not found", id);
            return ResponseEntity.notFound().build();
        }
        log.info("DELETE /loans/{} deleted", id);
        return ResponseEntity.noContent().build();
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
        if (dto.getId() != null) {
            loan.setId(dto.getId().intValue());
        }
        loan.setLoanDate(dto.getLoanDate());
        loan.setReturnDate(dto.getReturnDate());
        return loan;
    }
}
