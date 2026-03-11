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

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
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

    public Optional<AuthorDto> updateAuthor(Long id, AuthorDto authorDto) {
        Author existing = authorRepository.findById(id.intValue()).orElse(null);
        if (existing == null) {
            log.warn("Update author failed: id={} not found", id);
            return Optional.empty();
        }

        if (authorDto.getBirthDate() != null && !authorDto.getBirthDate().isBefore(LocalDate.now())) {
            log.warn("Update author id={} failed: birthDate must be in the past", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Birth date must be in the past");
        }

        Author updated = mapDtoToEntity(authorDto);
        updated.setId(existing.getId());

        Author saved = authorRepository.save(updated);
        log.info("Updated author id={}", id);
        return Optional.of(mapEntityToDto(saved));
    }

    private AuthorDto mapEntityToDto(Author author) {
        AuthorDto dto = new AuthorDto();
        dto.setId(author.getId() != null ? author.getId().longValue() : null);
        dto.setFirstName(author.getFirstName());
        dto.setLastName(author.getLastName());
        dto.setNationality(author.getNationality());
        dto.setDescription(author.getDescription());
        dto.setBirthDate(author.getBirthDate());
        return dto;
    }

    private Author mapDtoToEntity(AuthorDto dto) {
        Author entity = new Author();
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        if (dto.getNationality() != null) {
            entity.setNationality(dto.getNationality());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getBirthDate() != null) {
            entity.setBirthDate(dto.getBirthDate());
        }
        return entity;
    }
}
