package com.example.rlibrarybackend.service;

import com.example.rlibrarybackend.dto.LibraryDto;
import com.example.rlibrarybackend.model.Library;
import com.example.rlibrarybackend.repository.LibraryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class LibraryService {
    private final LibraryRepository libraryRepository;

    public LibraryService(LibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }

    public List<LibraryDto> getAllLibraries() {
        return libraryRepository.findAll().stream()
                .map(this::mapEntityToDto)
                .toList();
    }

    public Optional<LibraryDto> findLibraryById(Long id) {
        return libraryRepository.findById(id.intValue()).map(this::mapEntityToDto);
    }

    public LibraryDto createLibrary(LibraryDto dto) {
        // Check if library with same name already exists
        if (dto.getName() != null && libraryRepository.findByName(dto.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A library with this name already exists");
        }
        Library entity = mapDtoToEntity(dto);
        Library saved = libraryRepository.save(entity);
        return mapEntityToDto(saved);
    }

    public boolean deleteLibrary(Long id) {
        Integer entityId = id.intValue();
        if (!libraryRepository.existsById(entityId)) {
            return false;
        }
        libraryRepository.deleteById(entityId);
        return true;
    }

    private LibraryDto mapEntityToDto(Library library) {
        LibraryDto dto = new LibraryDto();
        dto.setId(library.getId() != null ? library.getId().longValue() : null);
        dto.setName(library.getName());
        dto.setCountry(library.getCountry());
        dto.setCity(library.getCity());
        dto.setStreet(library.getStreet());
        dto.setStreetNumber(library.getStreetNumber());
        dto.setDescription(library.getDescription());
        if (library.getLibraryStats() != null && library.getLibraryStats().getId() != null) {
            dto.setLibraryStatsId(library.getLibraryStats().getId().longValue());
        }
        if (library.getLibraryRule() != null && library.getLibraryRule().getId() != null) {
            dto.setLibraryRuleId(library.getLibraryRule().getId().longValue());
        }
        if (library.getBooks() != null) {
            dto.setBookIds(library.getBooks().stream()
                    .filter(book -> book.getId() != null)
                    .map(book -> book.getId().longValue())
                    .toList());
        }
        return dto;
    }

    private Library mapDtoToEntity(LibraryDto dto) {
        Library library = new Library();
        if (dto.getId() != null) {
            library.setId(dto.getId().intValue());
        }
        library.setName(dto.getName());
        library.setCountry(dto.getCountry());
        library.setCity(dto.getCity());
        library.setStreet(dto.getStreet());
        library.setStreetNumber(dto.getStreetNumber());
        library.setDescription(dto.getDescription());
        return library;
    }
}

