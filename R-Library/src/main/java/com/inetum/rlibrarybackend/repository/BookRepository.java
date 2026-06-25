package com.inetum.rlibrarybackend.repository;

import com.inetum.rlibrarybackend.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {
    List<Book> findByLibraryId(Integer libraryId);
    Page<Book> findByLibraryId(Integer libraryId, Pageable pageable);
    Optional<Book> findByIsbn(String isbn);
}
