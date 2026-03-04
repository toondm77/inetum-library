package com.example.rlibrarybackend.service;

import com.example.rlibrarybackend.dto.BookDto;
import com.example.rlibrarybackend.model.Author;
import com.example.rlibrarybackend.model.Book;
import com.example.rlibrarybackend.model.Library;
import com.example.rlibrarybackend.model.ThemeType;
import com.example.rlibrarybackend.repository.AuthorRepository;
import com.example.rlibrarybackend.repository.BookRepository;
import com.example.rlibrarybackend.repository.LibraryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Slf4j
public class BookService {
    private final BookRepository bookRepository;
    private final LibraryRepository libraryRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, LibraryRepository libraryRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.libraryRepository = libraryRepository;
        this.authorRepository = authorRepository;
    }

    public Page<BookDto> getAllBooks(int page, int size, String sort, String direction, String title, Integer minAmountOfPages, Integer maxAmountOfPages, ThemeType themeType, Long libraryId) {
        log.debug("Fetching all books page={} size={} sort={} direction={} title={} minAmountOfPages={} maxAmountOfPages={} themeType={} libraryId={}", page, size, sort, direction, title, minAmountOfPages, maxAmountOfPages, themeType, libraryId);
        Specification<Book> spec = Specification.where(null);
        if (title != null && !title.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), "%%" + title.toLowerCase() + "%%"));
        }
        if (minAmountOfPages != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("amountOfPages"), minAmountOfPages));
        }
        if (maxAmountOfPages != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("amountOfPages"), maxAmountOfPages));
        }
        if (themeType != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("theme"), themeType));
        }
        if (libraryId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("library").get("id"), libraryId.intValue()));
        }
        Sort sortOrder = Sort.by(Sort.Direction.fromString(direction != null ? direction : "asc"), sort != null && !sort.isBlank() ? sort : "title");
        return bookRepository.findAll(spec, PageRequest.of(page, size, sortOrder))
                .map(this::mapEntityToDto);
    }

    public List<BookDto> getAllBooks() {
        log.debug("Fetching all books");
        List<BookDto> books = bookRepository.findAll().stream()
                .map(this::mapEntityToDto)
                .toList();
        log.debug("Fetched {} books", books.size());
        return books;
    }

    public Optional<BookDto> findBookById(Long id) {
        log.debug("Finding book id={}", id);
        return bookRepository.findById(id.intValue())
                .map(this::mapEntityToDto)
                .or(Optional::empty);
    }


    public boolean deleteBook(Long id) {
        log.info("Deleting book id={}", id);
        return bookRepository.findById(id.intValue())
                .map(entity -> {
                    bookRepository.delete(entity);
                    log.info("Deleted book id={}", id);
                    return true;
                })
                .orElseGet(() -> {
                    log.warn("Delete book failed: id={} not found", id);
                    return false;
                });
    }

    public BookDto createBook(BookDto bookDto) {
        if (bookDto == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Book payload is required");
        }

        if (bookDto.getLibraryId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Library id is required");
        }
        Library library = libraryRepository.findById(bookDto.getLibraryId().intValue())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Library not found"));

        if (bookDto.getAuthorId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Author id is required");
        }
        Author author = authorRepository.findById(bookDto.getAuthorId().intValue())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Author not found"));

        if (bookDto.getPublishedYear() == null || bookDto.getPublishedYear() < 1400 || bookDto.getPublishedYear() > 2100) {
            throw new ResponseStatusException(BAD_REQUEST, "Published year must be between 1400 and 2100");
        }

        Book entity = mapDtoToEntity(bookDto, library, author);
        Book saved = bookRepository.save(entity);
        log.info("Created book id={}", saved.getId());
        return mapEntityToDto(saved);
    }

    public Page<BookDto> getBooksByLibraryId(Long libraryId, int page, int size) {
        log.debug("Fetching books for library id={} page={} size={}", libraryId, page, size);
        Page<BookDto> books = bookRepository.findByLibraryId(libraryId.intValue(), PageRequest.of(page, size))
                .map(this::mapEntityToDto);
        log.debug("Fetched {} books for library id={}", books.getContent().size(), libraryId);
        return books;
    }

    public List<BookDto> getBooksByLibraryId(Long libraryId) {
        log.debug("Fetching books for library id={}", libraryId);
        List<BookDto> books = bookRepository.findByLibraryId(libraryId.intValue()).stream()
                .map(this::mapEntityToDto)
                .toList();
        log.debug("Fetched {} books for library id={}", books.size(), libraryId);
        return books;
    }

    public boolean libraryExists(Long libraryId) {
        boolean exists = libraryId != null && libraryRepository.existsById(libraryId.intValue());
        log.debug("Library id={} exists={}", libraryId, exists);
        return exists;
    }

    private BookDto mapEntityToDto(Book book) {
        BookDto dto = new BookDto();
        if (book.getId() != null) {
            dto.setId(book.getId().longValue());
        }
        dto.setTitle(book.getTitle());
        dto.setDescription(book.getDescription());
        dto.setAmountOfPages(book.getAmountOfPages());
        dto.setIsbn(book.getIsbn());
        dto.setPublishedYear(book.getPublicationYear());
        dto.setReleaseDate(book.getReleaseDate());
        if (book.getTheme() != null) {
            dto.setTheme(com.example.rlibrarybackend.dto.ThemeType.valueOf(book.getTheme().name()));
        }
        if (book.getBookState() != null) {
            dto.setBookState(com.example.rlibrarybackend.dto.BookState.valueOf(book.getBookState().name()));
        }
        dto.setAgeCategory(book.getAgeCategory());
        dto.setPurchasePrice(book.getPurchasePrice());
        dto.setDuplicates(book.getDuplicates());
        dto.setAuthorName(book.getAuthor());

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

    private Book mapDtoToEntity(BookDto dto, Library library, Author author) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setDescription(dto.getDescription());
        book.setIsbn(dto.getIsbn());
        book.setPublicationYear(dto.getPublishedYear());
        book.setAmountOfPages(dto.getAmountOfPages() != null ? dto.getAmountOfPages() : 1);
        book.setReleaseDate(dto.getReleaseDate());
        if (dto.getTheme() != null) {
            book.setTheme(ThemeType.valueOf(dto.getTheme().name()));
        }
        if (dto.getBookState() != null) {
            book.setBookState(com.example.rlibrarybackend.model.BookState.valueOf(dto.getBookState().name()));
        }
        book.setAgeCategory(dto.getAgeCategory());
        book.setPurchasePrice(dto.getPurchasePrice() != null ? dto.getPurchasePrice() : 0.0);
        book.setDuplicates(dto.getDuplicates() != null ? dto.getDuplicates() : 0);

        book.setLibrary(library);
        book.setAuthor(author.getFirstName() + " " + author.getLastName());
        book.setAuthors(List.of(author));
        return book;
    }
}
