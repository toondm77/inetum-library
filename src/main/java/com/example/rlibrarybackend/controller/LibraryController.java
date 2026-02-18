package com.example.rlibrarybackend.controller;

import com.example.rlibrarybackend.api.LibrariesApi;
import com.example.rlibrarybackend.dto.BookDto;
import com.example.rlibrarybackend.dto.LibraryDto;
import com.example.rlibrarybackend.service.BookService;
import com.example.rlibrarybackend.service.LibraryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class LibraryController implements LibrariesApi {

    private final LibraryService libraryService;
    private final BookService bookService;

    public LibraryController(LibraryService libraryService, BookService bookService) {
        this.libraryService = libraryService;
        this.bookService = bookService;
    }

    @Override
    public ResponseEntity<List<LibraryDto>> librariesGet() {
        log.info("GET /libraries requested");
        List<LibraryDto> libraries = libraryService.getAllLibraries();
        log.debug("GET /libraries returned {} items", libraries.size());
        return ResponseEntity.ok(libraries);
    }

    @Override
    public ResponseEntity<LibraryDto> librariesIdGet(Long id) {
        log.info("GET /libraries/{} requested", id);
        return libraryService.findLibraryById(id)
                .map(dto -> {
                    log.debug("GET /libraries/{} found", id);
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    log.warn("GET /libraries/{} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Override
    public ResponseEntity<LibraryDto> librariesPost(LibraryDto libraryDto) {
        log.info("POST /libraries requested name={}", libraryDto.getName());
        LibraryDto created = libraryService.createLibrary(libraryDto);
        log.info("POST /libraries created id={}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<Void> librariesIdDelete(Long id) {
        log.info("DELETE /libraries/{} requested", id);
        boolean deleted = libraryService.deleteLibrary(id);
        if (!deleted) {
            log.warn("DELETE /libraries/{} not found", id);
            return ResponseEntity.notFound().build();
        }
        log.info("DELETE /libraries/{} deleted", id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<BookDto>> librariesIdBooksGet(Long id) {
        log.info("GET /libraries/{}/books requested", id);
        if (libraryService.findLibraryById(id).isEmpty()) {
            log.warn("GET /libraries/{}/books not found", id);
            return ResponseEntity.notFound().build();
        }
        List<BookDto> books = bookService.getBooksByLibraryId(id);
        log.debug("GET /libraries/{}/books returned {} items", id, books.size());
        return ResponseEntity.ok(books);
    }
}
