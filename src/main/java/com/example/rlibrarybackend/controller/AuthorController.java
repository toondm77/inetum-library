package com.example.rlibrarybackend.controller;

import com.example.rlibrarybackend.api.AuthorsApi;
import com.example.rlibrarybackend.dto.AuthorDto;
import com.example.rlibrarybackend.service.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AuthorController implements AuthorsApi {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Override
    public ResponseEntity<List<AuthorDto>> authorsGet() {
        return ResponseEntity.ok(authorService.getAllAuthors());
    }

    @Override
    public ResponseEntity<AuthorDto> authorsPost(AuthorDto authorDto) {
        AuthorDto created = authorService.createAuthor(authorDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<AuthorDto> authorsIdGet(Long id) {
        return authorService.findAuthorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> authorsIdDelete(Long id) {
        boolean deleted = authorService.deleteAuthor(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
