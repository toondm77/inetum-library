package com.example.rlibrarybackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Library {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String country;
    private String city;
    private String street;
    private String streetNumber;
    private String description;

    @OneToMany(mappedBy = "library")
    private List<Book> books = new ArrayList<>();

    @OneToOne(mappedBy = "library")
    private LibraryStats libraryStats;

    @OneToOne(mappedBy = "library")
    private LibraryRule libraryRule;
}

