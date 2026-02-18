package com.example.rlibrarybackend.repository;

import com.example.rlibrarybackend.model.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Integer> {
}

