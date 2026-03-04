package com.inetum.rlibrarybackend.service;

import com.inetum.rlibrarybackend.dto.AuthorDto;
import com.inetum.rlibrarybackend.model.Author;
import com.inetum.rlibrarybackend.repository.AuthorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Page<AuthorDto> getAllAuthors(int page, int size, String sort, String direction, String lastName, String nationality) {
        log.debug("Fetching authors page={} size={} sort={} direction={} lastName={} nationality={}", page, size, sort, direction, lastName, nationality);
        Specification<Author> spec = Specification.where(null);
        if (lastName != null && !lastName.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("lastName")), "%%" + lastName.toLowerCase() + "%%"));
        }
        if (nationality != null && !nationality.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("nationality")), "%%" + nationality.toLowerCase() + "%%"));
        }
        Sort sortOrder = Sort.by(Sort.Direction.fromString(direction != null ? direction : "asc"), sort != null && !sort.isBlank() ? sort : "id");
        return authorRepository.findAll(spec, PageRequest.of(page, size, sortOrder))
                .map(this::mapEntityToDto);
    }

    public List<AuthorDto> getAllAuthors(int page, int size) {
        log.debug("Fetching authors page={} size={}", page, size);
        List<AuthorDto> authors = authorRepository.findAll(PageRequest.of(page, size)).stream()
                .map(this::mapEntityToDto)
                .toList();
        log.debug("Fetched {} authors", authors.size());
        return authors;
    }

    public AuthorDto createAuthor(AuthorDto authorDto) {
        log.debug("Creating author");
        Author entity = mapDtoToEntity(authorDto);
        Author saved = authorRepository.save(entity);
        log.info("Created author id={}", saved.getId());
        return mapEntityToDto(saved);
    }

    public Optional<AuthorDto> findAuthorById(Long id) {
        log.debug("Finding author id={}", id);
        return authorRepository.findById(id.intValue()).map(this::mapEntityToDto);
    }

    public boolean deleteAuthor(Long id) {
        Integer entityId = id.intValue();
        if (!authorRepository.existsById(entityId)) {
            log.warn("Author id={} not found for delete", id);
            return false;
        }
        authorRepository.deleteById(entityId);
        log.info("Deleted author id={}", id);
        return true;
    }

    private AuthorDto mapEntityToDto(Author author) {
        AuthorDto dto = new AuthorDto();
        dto.setId(author.getId() != null ? author.getId().longValue() : null);
        dto.setFirstName(author.getFirstName());
        dto.setLastName(author.getLastName());
        return dto;
    }

    private Author mapDtoToEntity(AuthorDto dto) {
        Author entity = new Author();
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        return entity;
    }
}
