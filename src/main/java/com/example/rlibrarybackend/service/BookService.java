package com.example.rlibrarybackend.service;

import com.example.rlibrarybackend.dto.BookDto;
import com.example.rlibrarybackend.model.Book;
import com.example.rlibrarybackend.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::mapEntityToDto)
                .toList();
    }

    public Optional<BookDto> findBookById(Long id) {
        return bookRepository.findById(id.intValue()).map(this::mapEntityToDto);
    }

    public BookDto createBook(BookDto bookDto) {
        Book entity = mapDtoToEntity(bookDto);
        Book saved = bookRepository.save(entity);
        return mapEntityToDto(saved);
    }

    private BookDto mapEntityToDto(Book book) {
        BookDto dto = new BookDto();
        if (book.getId() != null) {
            dto.setId(book.getId().longValue());
        }
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setPublishedYear(book.getPublicationYear());
        if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
            var firstAuthor = book.getAuthors().getFirst();
            if (firstAuthor.getId() != null) {
                dto.setAuthorId(firstAuthor.getId().longValue());
            }
        }
        return dto;
    }

    private Book mapDtoToEntity(BookDto dto) {
        Book book = new Book();
        if (dto.getId() != null) {
            book.setId(dto.getId().intValue());
        }
        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setPublicationYear(dto.getPublishedYear() != null ? dto.getPublishedYear() : 0);
        return book;
    }
}

