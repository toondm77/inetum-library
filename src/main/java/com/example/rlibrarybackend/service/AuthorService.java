package com.example.rlibrarybackend.service;

import com.example.rlibrarybackend.dto.AuthorDto;
import com.example.rlibrarybackend.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<AuthorDto> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(this::mapEntityToDto)
                .toList();
    }

    public AuthorDto createAuthor(AuthorDto authorDto) {
        com.example.rlibrarybackend.model.Author entity = mapDtoToEntity(authorDto);
        com.example.rlibrarybackend.model.Author saved = authorRepository.save(entity);
        return mapEntityToDto(saved);
    }

    public Optional<AuthorDto> findAuthorById(Long id) {
        return authorRepository.findById(id.intValue()).map(this::mapEntityToDto);
    }

    public boolean deleteAuthor(Long id) {
        Integer entityId = id.intValue();
        if (!authorRepository.existsById(entityId)) {
            return false;
        }
        authorRepository.deleteById(entityId);
        return true;
    }

    private AuthorDto mapEntityToDto(com.example.rlibrarybackend.model.Author author) {
        AuthorDto dto = new AuthorDto();
        dto.setId(author.getId() != null ? author.getId().longValue() : null);
        dto.setFirstName(author.getFirstName());
        dto.setLastName(author.getLastName());
        return dto;
    }

    private com.example.rlibrarybackend.model.Author mapDtoToEntity(AuthorDto dto) {
        com.example.rlibrarybackend.model.Author entity = new com.example.rlibrarybackend.model.Author();
        if (dto.getId() != null) {
            entity.setId(dto.getId().intValue());
        }
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        return entity;
    }
}
