package com.inetum.rlibrarybackend.service;

import com.inetum.rlibrarybackend.dto.BookDto;
import com.inetum.rlibrarybackend.model.Author;
import com.inetum.rlibrarybackend.model.Book;
import com.inetum.rlibrarybackend.model.Library;
import com.inetum.rlibrarybackend.repository.AuthorRepository;
import com.inetum.rlibrarybackend.repository.BookRepository;
import com.inetum.rlibrarybackend.repository.LibraryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookService bookService;

    private BookDto dto;
    private Library library;
    private Author author;

    @BeforeEach
    void setUp() {
        dto = new BookDto();
        dto.setTitle("1984");
        dto.setIsbn("978-0451524935");
        dto.setPublishedYear(1949);
        dto.setLibraryId(1L);
        dto.setAuthorId(2L);

        library = new Library();
        library.setId(1);
        library.setName("Central");

        author = new Author();
        author.setId(2);
        author.setFirstName("George");
        author.setLastName("Orwell");
    }

    @Test
    void createBook_throwsWhenPayloadNull() {
        assertThrows(ResponseStatusException.class, () -> bookService.createBook(null));
        verifyNoInteractions(bookRepository, libraryRepository, authorRepository);
    }

    @Test
    void createBook_throwsWhenLibraryIdMissing() {
        dto.setLibraryId(null);
        assertThrows(ResponseStatusException.class, () -> bookService.createBook(dto));
        verifyNoInteractions(bookRepository);
    }

    @Test
    void createBook_throwsWhenLibraryNotFound() {
        when(libraryRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> bookService.createBook(dto));
        verify(libraryRepository).findById(1);
        verifyNoInteractions(bookRepository);
    }

    @Test
    void createBook_throwsWhenAuthorIdMissing() {
        when(libraryRepository.findById(1)).thenReturn(Optional.of(library));
        dto.setAuthorId(null);
        assertThrows(ResponseStatusException.class, () -> bookService.createBook(dto));
        verifyNoInteractions(bookRepository);
    }

    @Test
    void createBook_throwsWhenAuthorNotFound() {
        when(libraryRepository.findById(1)).thenReturn(Optional.of(library));
        when(authorRepository.findById(2)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> bookService.createBook(dto));
        verifyNoInteractions(bookRepository);
    }

    @Test
    void createBook_throwsWhenYearInvalid() {
        when(libraryRepository.findById(1)).thenReturn(Optional.of(library));
        when(authorRepository.findById(2)).thenReturn(Optional.of(author));
        dto.setPublishedYear(1300);
        assertThrows(ResponseStatusException.class, () -> bookService.createBook(dto));
        verifyNoInteractions(bookRepository);
    }

    @Test
    void createBook_succeeds() {
        when(libraryRepository.findById(1)).thenReturn(Optional.of(library));
        when(authorRepository.findById(2)).thenReturn(Optional.of(author));
        Book saved = new Book();
        saved.setId(10);
        saved.setTitle(dto.getTitle());
        saved.setIsbn(dto.getIsbn());
        saved.setPublicationYear(dto.getPublishedYear());
        saved.setLibrary(library);
        saved.setAuthors(List.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(saved);

        BookDto result = bookService.createBook(dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(dto.getTitle(), result.getTitle());
        assertEquals(dto.getLibraryId(), result.getLibraryId());
        assertEquals(dto.getAuthorId(), result.getAuthorId());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void deleteBook_returnsFalse_whenNotFound() {
        when(bookRepository.findById(5)).thenReturn(Optional.empty());
        boolean result = bookService.deleteBook(5L);
        assertFalse(result);
        verify(bookRepository, never()).delete(any(Book.class));
    }

    @Test
    void deleteBook_returnsTrue_whenDeleted() {
        Book book = new Book();
        book.setId(5);
        when(bookRepository.findById(5)).thenReturn(Optional.of(book));
        boolean result = bookService.deleteBook(5L);
        assertTrue(result);
        verify(bookRepository).delete(book);
    }
}
