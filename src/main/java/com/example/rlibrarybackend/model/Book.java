package com.example.rlibrarybackend.model;

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

    @ManyToOne
    @JoinColumn(name = "library_id")
    private Library library;

    private String title;
    private String author;
    private String description;
    private String isbn;
    private int publicationYear;
    private int amountOfPages;
    private LocalDate releaseDate;

    @Enumerated(EnumType.STRING)
    private ThemeType theme;

    @Enumerated(EnumType.STRING)
    private BookState bookState;

    private String ageCategory;
    private double purchasePrice;
    private int dupplicates;

    @ManyToMany
    private List<Author> authors = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    private List<BookComplaint> bookComplaints = new ArrayList<>();
}
