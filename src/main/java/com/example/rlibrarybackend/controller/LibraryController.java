package com.example.rlibrarybackend.controller;

import com.example.rlibrarybackend.api.LibrariesApi;
import com.example.rlibrarybackend.dto.BookDto;
import com.example.rlibrarybackend.dto.LibraryDto;
import com.example.rlibrarybackend.service.BookService;
import com.example.rlibrarybackend.service.LibraryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LibraryController implements LibrariesApi {

    private final LibraryService libraryService;
    private final BookService bookService;

    public LibraryController(LibraryService libraryService, BookService bookService) {
        this.libraryService = libraryService;
        this.bookService = bookService;
    }

    @Override
    public ResponseEntity<List<LibraryDto>> librariesGet() {
        return ResponseEntity.ok(libraryService.getAllLibraries());
    }

    @Override
    public ResponseEntity<LibraryDto> librariesIdGet(Long id) {
        return libraryService.findLibraryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<LibraryDto> librariesPost(LibraryDto libraryDto) {
        LibraryDto created = libraryService.createLibrary(libraryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<Void> librariesIdDelete(Long id) {
        boolean deleted = libraryService.deleteLibrary(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<BookDto>> librariesIdBooksGet(Long id) {
        if (libraryService.findLibraryById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(bookService.getBooksByLibraryId(id));
    }
}
