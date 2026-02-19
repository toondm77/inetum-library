package com.example.rlibrarybackend.controller;

import com.example.rlibrarybackend.api.BooksApi;
import com.example.rlibrarybackend.dto.BookDto;
import com.example.rlibrarybackend.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
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
    public ResponseEntity<List<BookDto>> booksGet(Integer page, Integer size, String sort, String direction, String title, Long libraryId) {
        int p = page != null ? page : 0;
        int s = size != null ? size : 20;
        String sortField = (sort != null && !sort.isBlank()) ? sort : "title";
        String dir = (direction != null && !direction.isBlank()) ? direction : "asc";
        log.info("GET /books requested page={} size={} sort={} direction={}", p, s, sortField, dir);
        List<BookDto> books = bookService.getAllBooks(p, s, sortField, dir, title, libraryId);
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
    public ResponseEntity<BookDto> booksPost(@Valid @RequestBody BookDto bookDto) {
        log.info("POST /books requested");
        BookDto created = bookService.createBook(bookDto);
        log.info("POST /books created id={}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<Void> booksIdDelete(Long id) {
        log.info("DELETE /books/{} requested", id);
        boolean deleted = bookService.deleteBook(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        log.warn("DELETE /books/{} not found", id);
        return ResponseEntity.notFound().build();
    }
}
