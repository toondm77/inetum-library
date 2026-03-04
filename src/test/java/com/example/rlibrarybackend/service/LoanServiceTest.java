package com.example.rlibrarybackend.service;

import com.example.rlibrarybackend.model.Loan;
import com.example.rlibrarybackend.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    private Loan loan;

    @BeforeEach
    void setUp() {
        loan = new Loan();
        loan.setId(1);
    }

    @Test
    void findLoanById_returnsLoan_whenPresent() {
        when(loanRepository.findById(1)).thenReturn(Optional.of(loan));

        var result = loanService.findLoanById(1L);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        verify(loanRepository).findById(1);
    }

    @Test
    void findLoanById_returnsEmpty_whenMissing() {
        when(loanRepository.findById(2)).thenReturn(Optional.empty());

        var result = loanService.findLoanById(2L);
        assertTrue(result.isEmpty());
    }


    @Test
    void deleteLoan_returnsTrue_whenExists() {
        when(loanRepository.existsById(1)).thenReturn(true);

        boolean deleted = loanService.deleteLoan(1);
        assertTrue(deleted);
        verify(loanRepository).deleteById(1);
    }

    @Test
    void deleteLoan_returnsFalse_whenMissing() {
        when(loanRepository.existsById(3)).thenReturn(false);

        boolean deleted = loanService.deleteLoan(3);
        assertFalse(deleted);
        verify(loanRepository, never()).deleteById(any());
    }
}

