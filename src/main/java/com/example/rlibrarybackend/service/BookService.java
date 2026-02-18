package com.example.rlibrarybackend.service;

import com.example.rlibrarybackend.dto.BookDto;
import com.example.rlibrarybackend.model.Author;
import com.example.rlibrarybackend.model.Book;
import com.example.rlibrarybackend.repository.BookRepository;
import com.example.rlibrarybackend.repository.LibraryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final LibraryRepository libraryRepository;

    public BookService(BookRepository bookRepository, LibraryRepository libraryRepository) {
        this.bookRepository = bookRepository;
        this.libraryRepository = libraryRepository;
    }

    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::mapEntityToDto)
                .toList();
    }

    public Optional<BookDto> findBookById(Long id) {
        return bookRepository.findById(id.intValue())
                .map(this::mapEntityToDto)
                .or(Optional::empty);
    }

    public Optional<BookDto> createBook(BookDto bookDto) {
        if (bookDto.getLibraryId() != null
                && !libraryRepository.existsById(bookDto.getLibraryId().intValue())) {
            return Optional.empty();
        }
        Book entity = mapDtoToEntity(bookDto);
        Book saved = bookRepository.save(entity);
        return Optional.of(mapEntityToDto(saved));
    }

    public List<BookDto> getBooksByLibraryId(Long libraryId) {
        return bookRepository.findByLibraryId(libraryId.intValue()).stream()
                .map(this::mapEntityToDto)
                .toList();
    }

    public boolean libraryExists(Long libraryId) {
        return libraryId != null && libraryRepository.existsById(libraryId.intValue());
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
            Author firstAuthor = book.getAuthors().getFirst();
            if (firstAuthor.getId() != null) {
                dto.setAuthorId(firstAuthor.getId().longValue());
            }
        }
        if (book.getLibrary() != null && book.getLibrary().getId() != null) {
            dto.setLibraryId(book.getLibrary().getId().longValue());
            dto.setLibraryName(book.getLibrary().getName());
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
        if (dto.getLibraryId() != null) {
            libraryRepository.findById(dto.getLibraryId().intValue())
                    .ifPresent(book::setLibrary);
        }
        return book;
    }
}
