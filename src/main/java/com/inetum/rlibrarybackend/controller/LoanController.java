package com.inetum.rlibrarybackend.controller;

import com.inetum.rlibrarybackend.api.LoansApi;
import com.inetum.rlibrarybackend.dto.LoanDto;
import com.inetum.rlibrarybackend.dto.PagedLoanResponse;
import com.inetum.rlibrarybackend.service.CurrentUserService;
import com.inetum.rlibrarybackend.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class LoanController implements LoansApi {

    private final LoanService loanService;
    private final CurrentUserService currentUserService;

    public LoanController(LoanService loanService, CurrentUserService currentUserService) {
        this.loanService = loanService;
        this.currentUserService = currentUserService;
    }

    @Override
    @PreAuthorize("hasAnyRole('WERKNEMER', 'VERANTWOORDELIJKE')")
    public ResponseEntity<PagedLoanResponse> loansGet(Integer page, Integer size, String sort, String direction, String status, Long personId, LocalDate loanDateFrom, LocalDate loanDateTo) {
        int p = page != null ? page : 0;
        int s = size != null ? size : 20;
        String sortField = (sort != null && !sort.isBlank()) ? sort : "loanDate";
        String dir = (direction != null && !direction.isBlank()) ? direction : "asc";

        if (!currentUserService.currentUserIsStaff()) {
            var currentPerson = currentUserService.getCurrentPerson();
            Long ownPersonId = currentPerson.getId().longValue();
            if (personId != null && !personId.equals(ownPersonId)) {
                log.warn("Access denied: personId={} tried to list loans for personId={}", ownPersonId, personId);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own loans");
            }
            personId = ownPersonId;
        }

        log.info("GET /loans requested page={} size={} sort={} direction={}", p, s, sortField, dir);
        Page<LoanDto> loansPage = loanService.getAllLoans(p, s, sortField, dir, status, personId, loanDateFrom, loanDateTo);
        List<LoanDto> items = loansPage.getContent();
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
                .map(dto -> {
                    if (!currentUserService.currentUserIsStaff()) {
                        var currentPerson = currentUserService.getCurrentPerson();
                        if (!Long.valueOf(currentPerson.getId()).equals(dto.getPersonId())) {
                            log.warn("Access denied: personId={} tried to access loanId={}", currentPerson.getId(), id);
                            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own loans");
                        }
                    }
                    log.debug("GET /loans/{} found", id);
                    return ResponseEntity.ok(dto);
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
        LoanDto created = loanService.createLoan(loanDto);
        log.info("POST /loans created id={}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
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
}
