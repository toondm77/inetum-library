package com.inetum.rlibrarybackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.*;
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
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "library_id")
    private Library library;

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    @Size(max = 100)
    private String author;

    @Size(max = 1000)
    private String description;

    @NotBlank
    @Size(min = 10, max = 17)
    private String isbn;

    @Min(1400)
    @Max(2100)
    private int publicationYear;

    @Min(1)
    @Max(10000)
    private int amountOfPages;

    @PastOrPresent
    private LocalDate releaseDate;

    @Enumerated(EnumType.STRING)
    private ThemeType theme;

    @Enumerated(EnumType.STRING)
    private BookState bookState;

    @Size(max = 50)
    private String ageCategory;

    @DecimalMin("0.0")
    private double purchasePrice;

    @Min(0)
    private int duplicates;

    @ManyToMany
    private List<Author> authors = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    private List<BookComplaint> bookComplaints = new ArrayList<>();
}
