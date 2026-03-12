package com.inetum.rlibrarybackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @PastOrPresent
    private LocalDateTime loanDate;

    @PastOrPresent
    private LocalDateTime returnDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "person_id")
    @JsonBackReference
    private Person person;

    @ManyToOne
    @JoinColumn(name = "loan_rule_id")
    private LoanRule loanRule;

    @NotNull
    @Size(min = 1)
    @ManyToMany
    @JoinTable(
            name = "loan_books",
            joinColumns = @JoinColumn(name = "loan_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> books = new ArrayList<>();
}
