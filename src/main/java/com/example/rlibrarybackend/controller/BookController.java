package com.example.rlibrarybackend.controller;

import com.example.rlibrarybackend.api.BooksApi;
import com.example.rlibrarybackend.dto.BookDto;
import com.example.rlibrarybackend.dto.PagedBookResponse;
import com.example.rlibrarybackend.dto.ThemeType;
import com.example.rlibrarybackend.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<PagedBookResponse> booksGet(Integer page, Integer size, String sort, String direction, String title, Integer minAmountOfPages, Integer maxAmountOfPages, ThemeType themeType, Long libraryId) {
        int p = page != null ? page : 0;//default staat ook in apispec
        int s = size != null ? size : 10;
        String sortField = (sort != null && !sort.isBlank()) ? sort : "title";
        String dir = (direction != null && !direction.isBlank()) ? direction : "asc";
        log.info("GET /books requested page={} size={} sort={} direction={} minPages={} maxPages={}", p, s, sortField, dir, minAmountOfPages, maxAmountOfPages);
        com.example.rlibrarybackend.model.ThemeType modelTheme = null;
        if (themeType != null) {
            try {
                modelTheme = com.example.rlibrarybackend.model.ThemeType.valueOf(themeType.name());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid theme type provided: {}", themeType);
            }
        }
        Page<BookDto> booksPage = bookService.getAllBooks(p, s, sortField, dir, title, minAmountOfPages, maxAmountOfPages, modelTheme, libraryId);
        PagedBookResponse response = new PagedBookResponse(
                booksPage.getNumber(),
                booksPage.getSize(),
                booksPage.getTotalElements(),
                booksPage.getTotalPages(),
                booksPage.getContent()
        );
        log.debug("GET /books returned {} items", booksPage.getContent().size());
        return ResponseEntity.ok(response);
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
