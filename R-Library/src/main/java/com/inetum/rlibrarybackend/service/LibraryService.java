package com.inetum.rlibrarybackend.service;

import com.inetum.rlibrarybackend.dto.LibraryDto;
import com.inetum.rlibrarybackend.model.Library;
import com.inetum.rlibrarybackend.model.LibraryRule;
import com.inetum.rlibrarybackend.model.LibraryStats;
import com.inetum.rlibrarybackend.repository.LibraryRepository;
import com.inetum.rlibrarybackend.repository.LibraryRuleRepository;
import com.inetum.rlibrarybackend.repository.LibraryStatsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Slf4j
public class LibraryService {
    private final LibraryRepository libraryRepository;
    private final LibraryRuleRepository libraryRuleRepository;
    private final LibraryStatsRepository libraryStatsRepository;

    public LibraryService(LibraryRepository libraryRepository,
                          LibraryRuleRepository libraryRuleRepository,
                          LibraryStatsRepository libraryStatsRepository) {
        this.libraryRepository = libraryRepository;
        this.libraryRuleRepository = libraryRuleRepository;
        this.libraryStatsRepository = libraryStatsRepository;
    }

    public Page<LibraryDto> getAllLibraries(int page, int size, String sort, String direction, String name, String city) {
        log.debug("Fetching all libraries page={} size={} sort={} direction={} name={} city={}", page, size, sort, direction, name, city);
        Specification<Library> spec = Specification.where(null);
        if (name != null && !name.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%%" + name.toLowerCase() + "%%"));
        }
        if (city != null && !city.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("city")), "%%" + city.toLowerCase() + "%%"));
        }
        Sort sortOrder = Sort.by(Sort.Direction.fromString(direction != null ? direction : "asc"), sort != null && !sort.isBlank() ? sort : "name");
        return libraryRepository.findAll(spec, PageRequest.of(page, size, sortOrder))
                .map(this::mapEntityToDto);
    }

    public Optional<LibraryDto> findLibraryById(Long id) {
        log.debug("Finding library id={}", id);
        return libraryRepository.findById(id.intValue()).map(this::mapEntityToDto);
    }

    public LibraryDto createLibrary(LibraryDto dto) {
        if (dto.getName() != null && libraryRepository.findByName(dto.getName()).isPresent()) {
            log.warn("Create library failed: name '{}' already exists", dto.getName());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A library with this name already exists");
        }
        if (dto.getId() != null && libraryRepository.existsById(dto.getId().intValue())) {
            log.warn("Create library failed: id '{}' already exists", dto.getId());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A library with this id already exists");
        }
        Library entity = mapDtoToEntity(dto);
        try {
            Library saved = libraryRepository.save(entity);
            log.info("Created library id={} name='{}'", saved.getId(), saved.getName());
            return mapEntityToDto(saved);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Create library failed: DataIntegrityViolationException");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A library with this data already exists", ex);
        }
    }

    public boolean deleteLibrary(Long id) {
        Integer entityId = id.intValue();
        if (!libraryRepository.existsById(entityId)) {
            log.warn("Library id={} not found for delete", id);
            return false;
        }
        libraryRepository.deleteById(entityId);
        log.info("Deleted library id={}", id);
        return true;
    }

    public Optional<LibraryDto> updateLibrary(Long id, LibraryDto dto) {
        Library existing = libraryRepository.findById(id.intValue()).orElse(null);
        if (existing == null) {
            log.warn("Update library failed: id={} not found", id);
            return Optional.empty();
        }

        if (dto.getLibraryRuleId() != null && dto.getLibraryRuleId() !=0) {
            LibraryRule rule = libraryRuleRepository.findById(dto.getLibraryRuleId().intValue())
                    .orElseThrow(() -> {
                        log.warn("Update library id={} failed: libraryRule id={} not found", id, dto.getLibraryRuleId());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "LibraryRule not found");
                    });
        }
        if (dto.getLibraryStatsId() != null && dto.getLibraryStatsId()!=0) {
            LibraryStats stats = libraryStatsRepository.findById(dto.getLibraryStatsId().intValue())
                    .orElseThrow(() -> {
                        log.warn("Update library id={} failed: libraryStats id={} not found", id, dto.getLibraryStatsId());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "LibraryStats not found");
                    });
        }

        existing.setName(dto.getName());
        existing.setCountry(dto.getCountry());
        existing.setCity(dto.getCity());
        existing.setStreet(dto.getStreet());
        existing.setStreetNumber(dto.getStreetNumber());
        existing.setDescription(dto.getDescription());

        Library saved = libraryRepository.save(existing);
        log.info("Updated library id={}", id);
        return Optional.of(mapEntityToDto(saved));
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
        library.setName(dto.getName());
        library.setCountry(dto.getCountry());
        library.setCity(dto.getCity());
        library.setStreet(dto.getStreet());
        library.setStreetNumber(dto.getStreetNumber());
        library.setDescription(dto.getDescription());
        return library;
    }
}
