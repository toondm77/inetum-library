package com.example.rlibrarybackend.controller;

import com.example.rlibrarybackend.model.Loan;
import com.example.rlibrarybackend.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("/loans")
    public ResponseEntity<List<Loan>> getAllLoans() {
        log.info("GET /loans requested");
        List<Loan> loans = loanService.getAllLoans();
        log.debug("GET /loans returned {} items", loans.size());
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/loans/{id}")
    public ResponseEntity<Loan> getLoanById(@PathVariable Long id) {
        log.info("GET /loans/{} requested", id);
        return loanService.findLoanById(id)
                .map(loan -> {
                    log.debug("GET /loans/{} found", id);
                    return ResponseEntity.ok(loan);
                })
                .orElseGet(() -> {
                    log.warn("GET /loans/{} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping("/loans")
    public ResponseEntity<Loan> createLoan(@RequestBody Loan loan) {
        log.info("POST /loans requested");
        Loan created = loanService.createLoan(loan);
        log.info("POST /loans created id={}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/loans/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Integer id) {
        log.info("DELETE /loans/{} requested", id);
        boolean deleted = loanService.deleteLoan(id);
        if (!deleted) {
            log.warn("DELETE /loans/{} not found", id);
            return ResponseEntity.notFound().build();
        }
        log.info("DELETE /loans/{} deleted", id);
        return ResponseEntity.noContent().build();
    }
}
