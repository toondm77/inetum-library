package com.example.rlibrarybackend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String functie;
    private String badgeCode;

    @ManyToOne
    @JoinColumn(name = "active_library_id")
    private Library activeLibrary;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @OneToOne(mappedBy = "person")
    private PersonalStats personalStats;

    @OneToMany(mappedBy = "person")
    @JsonManagedReference
    private List<Loan> loans = new ArrayList<>();

    @OneToMany(mappedBy = "person")
    private List<BookComplaint> bookComplaints = new ArrayList<>();
}
