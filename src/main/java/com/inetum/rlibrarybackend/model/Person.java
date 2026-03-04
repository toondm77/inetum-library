package com.inetum.rlibrarybackend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

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

    @NotBlank
    @Size(max = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    @NotNull
    @Past
    private LocalDate birthDate;

    @Size(max = 100)
    private String function;

    @Size(max = 100)
    private String badgeCode;

    @Column(unique = true)
    private String auth0Id;

    @Enumerated(EnumType.STRING)
    @Column(updatable = false)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Size(max = 100)
    private String country;

    @Size(max = 20)
    private String phoneNumber;

    @Size(max = 10)
    private String preferredLanguage;

    @Size(max = 500)
    private String profilePictureUrl;

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

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "person")
    private List<BookComplaint> bookComplaints = new ArrayList<>();
}
