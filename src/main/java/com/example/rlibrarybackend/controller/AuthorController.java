package com.example.rlibrarybackend.controller;

import com.example.rlibrarybackend.api.AuthorsApi;
import com.example.rlibrarybackend.dto.AuthorDto;
import com.example.rlibrarybackend.service.AuthorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class AuthorController implements AuthorsApi {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Override
    public ResponseEntity<List<AuthorDto>> authorsGet(Integer page, Integer size, String sort, String direction, String lastName, String nationality) {
        int p = page != null ? page : 0;
        int s = size != null ? size : 20;
        String sortField = (sort != null && !sort.isBlank()) ? sort : "id";
        String dir = (direction != null && !direction.isBlank()) ? direction : "asc";
        log.info("GET /authors requested page={} size={} sort={} direction={}", p, s, sortField, dir);
        List<AuthorDto> authors = authorService.getAllAuthors(p, s, sortField, dir, lastName, nationality);
        log.debug("GET /authors returned {} items", authors.size());
        return ResponseEntity.ok(authors);
    }

    @Override
    public ResponseEntity<AuthorDto> authorsPost(AuthorDto authorDto) {
        log.info("POST /authors requested");
        AuthorDto created = authorService.createAuthor(authorDto);
        log.info("POST /authors created id={}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<AuthorDto> authorsIdGet(Long id) {
        log.info("GET /authors/{} requested", id);
        return authorService.findAuthorById(id)
                .map(dto -> {
                    log.debug("GET /authors/{} found", id);
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    log.warn("GET /authors/{} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Override
    public ResponseEntity<Void> authorsIdDelete(Long id) {
        log.info("DELETE /authors/{} requested", id);
        boolean deleted = authorService.deleteAuthor(id);
        if (!deleted) {
            log.warn("DELETE /authors/{} not found", id);
            return ResponseEntity.notFound().build();
        }
        log.info("DELETE /authors/{} deleted", id);
        return ResponseEntity.noContent().build();
    }
}
