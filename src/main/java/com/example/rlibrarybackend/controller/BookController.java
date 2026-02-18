package com.example.rlibrarybackend.controller;

import com.example.rlibrarybackend.api.BooksApi;
import com.example.rlibrarybackend.dto.BookDto;
import com.example.rlibrarybackend.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class BookController implements BooksApi {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public ResponseEntity<List<BookDto>> booksGet() {
        log.info("GET /books requested");
        List<BookDto> books = bookService.getAllBooks();
        log.debug("GET /books returned {} items", books.size());
        return ResponseEntity.ok(books);
    }

    @Override
    public ResponseEntity<BookDto> booksIdGet(Long id) {
        log.info("GET /books/{} requested", id);
        return bookService.findBookById(id)
                .map(dto -> {
                    log.debug("GET /books/{} found", id);
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    log.warn("GET /books/{} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Override
    public ResponseEntity<BookDto> booksPost(BookDto bookDto) {
        log.info("POST /books requested");
        return bookService.createBook(bookDto)
                .map(created -> {
                    log.info("POST /books created id={}", created.getId());
                    return ResponseEntity.status(HttpStatus.CREATED).body(created);
                })
                .orElseGet(() -> {
                    log.warn("POST /books failed: library not found");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                });
    }
}
