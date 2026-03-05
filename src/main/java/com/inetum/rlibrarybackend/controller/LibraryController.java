package com.inetum.rlibrarybackend.controller;

import com.inetum.rlibrarybackend.api.LibrariesApi;
import com.inetum.rlibrarybackend.dto.BookDto;
import com.inetum.rlibrarybackend.dto.LibraryDto;
import com.inetum.rlibrarybackend.dto.PagedBookResponse;
import com.inetum.rlibrarybackend.dto.PagedLibraryResponse;
import com.inetum.rlibrarybackend.service.BookService;
import com.inetum.rlibrarybackend.service.LibraryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PreAuthorize("hasAnyRole('WERKNEMER', 'VERANTWOORDELIJKE')")
    public ResponseEntity<PagedLibraryResponse> librariesGet(Integer page, Integer size, String sort, String direction, String name, String city) {
        int p = page != null ? page : 0;
        int s = size != null ? size : 20;
        String sortField = (sort != null && !sort.isBlank()) ? sort : "name";
        String dir = (direction != null && !direction.isBlank()) ? direction : "asc";
        log.info("GET /libraries requested page={} size={} sort={} direction={}", p, s, sortField, dir);
        Page<LibraryDto> librariesPage = libraryService.getAllLibraries(p, s, sortField, dir, name, city);
        PagedLibraryResponse response = new PagedLibraryResponse(
                librariesPage.getNumber(),
                librariesPage.getSize(),
                librariesPage.getTotalElements(),
                librariesPage.getTotalPages(),
                librariesPage.getContent()
        );
        log.debug("GET /libraries returned {} items", librariesPage.getContent().size());
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasAnyRole('WERKNEMER', 'VERANTWOORDELIJKE')")
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
    @PreAuthorize("hasRole('VERANTWOORDELIJKE')")
    public ResponseEntity<LibraryDto> librariesPost(LibraryDto libraryDto) {
        log.info("POST /libraries requested name={}", libraryDto.getName());
        LibraryDto created = libraryService.createLibrary(libraryDto);
        log.info("POST /libraries created id={}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    @PreAuthorize("hasRole('VERANTWOORDELIJKE')")
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
    @PreAuthorize("hasRole('VERANTWOORDELIJKE')")
    public ResponseEntity<LibraryDto> librariesIdPut(Long id, LibraryDto libraryDto) {
        log.info("PUT /libraries/{} requested", id);
        return libraryService.updateLibrary(id, libraryDto)
                .map(dto -> {
                    log.info("PUT /libraries/{} updated", id);
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    log.warn("PUT /libraries/{} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Override
    @PreAuthorize("hasAnyRole('WERKNEMER', 'VERANTWOORDELIJKE')")
    public ResponseEntity<PagedBookResponse> librariesIdBooksGet(Long id, Integer page, Integer size) {
        int p = page != null ? page : 0;
        int s = size != null ? size : 20;
        log.info("GET /libraries/{}/books requested page={} size={}", id, p, s);
        if (libraryService.findLibraryById(id).isEmpty()) {
            log.warn("GET /libraries/{}/books not found", id);
            return ResponseEntity.notFound().build();
        }
        Page<BookDto> booksPage = bookService.getBooksByLibraryId(id, p, s);
        PagedBookResponse response = new PagedBookResponse(
                booksPage.getNumber(),
                booksPage.getSize(),
                booksPage.getTotalElements(),
                booksPage.getTotalPages(),
                booksPage.getContent()
        );
        log.debug("GET /libraries/{}/books returned {} items", id, booksPage.getContent().size());
        return ResponseEntity.ok(response);
    }
}
