package com.example.rlibrarybackend.service;

import com.example.rlibrarybackend.dto.BookDto;
import com.example.rlibrarybackend.model.Book;
import com.example.rlibrarybackend.repository.BookRepository;
import com.example.rlibrarybackend.repository.LibraryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LibraryRepository libraryRepository;

    @InjectMocks
    private BookService bookService;

    private BookDto dto;

    @BeforeEach
    void setUp() {
        dto = new BookDto();
        dto.setTitle("1984");
        dto.setIsbn("978-0451524935");
        dto.setPublishedYear(1949);
    }

    @Test
    void createBook_returnsEmpty_whenLibraryMissing() {
        dto.setLibraryId(5L);
        when(libraryRepository.existsById(5)).thenReturn(false);

        Optional<BookDto> result = bookService.createBook(dto);
        assertTrue(result.isEmpty());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void createBook_succeeds_whenLibraryExists() {
        dto.setLibraryId(1L);
        when(libraryRepository.existsById(1)).thenReturn(true);
        Book saved = new Book();
        saved.setId(10);
        saved.setTitle(dto.getTitle());
        saved.setIsbn(dto.getIsbn());
        saved.setPublicationYear(dto.getPublishedYear());

        when(bookRepository.save(any(Book.class))).thenReturn(saved);

        Optional<BookDto> result = bookService.createBook(dto);
        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getId());
        assertEquals(dto.getTitle(), result.get().getTitle());
        verify(bookRepository).save(any(Book.class));
    }
}

