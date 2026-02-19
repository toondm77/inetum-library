package com.example.rlibrarybackend.service;

import com.example.rlibrarybackend.dto.AuthorDto;
import com.example.rlibrarybackend.model.Author;
import com.example.rlibrarybackend.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    private Author entity;
    private AuthorDto dto;

    @BeforeEach
    void setUp() {
        entity = new Author();
        entity.setId(1);
        entity.setFirstName("George");
        entity.setLastName("Orwell");

        dto = new AuthorDto();
        dto.setFirstName("George");
        dto.setLastName("Orwell");
    }


    @Test
    void findAuthorById_returnsAuthor_whenPresent() {
        when(authorRepository.findById(1)).thenReturn(Optional.of(entity));

        var result = authorService.findAuthorById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(authorRepository).findById(1);
    }

    @Test
    void findAuthorById_returnsEmpty_whenMissing() {
        when(authorRepository.findById(2)).thenReturn(Optional.empty());

        var result = authorService.findAuthorById(2L);
        assertTrue(result.isEmpty());
    }

    @Test
    void createAuthor_saves_andReturnsDto() {
        when(authorRepository.save(any(Author.class))).thenReturn(entity);

        AuthorDto created = authorService.createAuthor(dto);
        assertNotNull(created.getId());
        assertEquals(dto.getFirstName(), created.getFirstName());
        verify(authorRepository).save(any(Author.class));
    }

    @Test
    void deleteAuthor_returnsTrue_whenExists() {
        when(authorRepository.existsById(1)).thenReturn(true);

        boolean deleted = authorService.deleteAuthor(1L);
        assertTrue(deleted);
        verify(authorRepository).deleteById(1);
    }

    @Test
    void deleteAuthor_returnsFalse_whenMissing() {
        when(authorRepository.existsById(3)).thenReturn(false);

        boolean deleted = authorService.deleteAuthor(3L);
        assertFalse(deleted);
        verify(authorRepository, never()).deleteById(any());
    }
}
