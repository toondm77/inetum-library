package com.example.rlibrarybackend.service;

import com.example.rlibrarybackend.dto.AuthorDto;
import com.example.rlibrarybackend.model.Author;
import com.example.rlibrarybackend.repository.AuthorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
        if (dto.getId() != null) {
            entity.setId(dto.getId().intValue());
        }
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        return entity;
    }
}
