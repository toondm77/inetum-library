package com.example.rlibrarybackend.service;

import com.example.rlibrarybackend.dto.LibraryDto;
import com.example.rlibrarybackend.model.Library;
import com.example.rlibrarybackend.repository.LibraryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private LibraryRepository libraryRepository;

    @InjectMocks
    private LibraryService libraryService;

    private LibraryDto dto;

    @BeforeEach
    void setUp() {
        dto = new LibraryDto();
        dto.setId(1L);
        dto.setName("Central Library");
        dto.setCountry("Belgium");
        dto.setCity("Brussels");
        dto.setStreet("Main Street");
        dto.setStreetNumber("12A");
    }

    @Test
    void createLibrary_throwsConflict_whenNameExists() {
        when(libraryRepository.findByName("Central Library")).thenReturn(Optional.of(new Library()));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> libraryService.createLibrary(dto));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(libraryRepository, never()).save(any());
    }

    @Test
    void createLibrary_throwsConflict_whenIdExists() {
        dto.setId(99L);
        when(libraryRepository.existsById(99)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> libraryService.createLibrary(dto));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(libraryRepository, never()).save(any());
    }

    @Test
    void createLibrary_throwsConflict_whenDataIntegrityViolation() {
        when(libraryRepository.findByName("Central Library")).thenReturn(Optional.empty());
        when(libraryRepository.save(any(Library.class))).thenThrow(new DataIntegrityViolationException("duplicate"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> libraryService.createLibrary(dto));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void createLibrary_succeeds_andReturnsDto() {
        Library saved = new Library();
        saved.setId(1);
        saved.setName(dto.getName());
        saved.setCountry(dto.getCountry());
        saved.setCity(dto.getCity());
        saved.setStreet(dto.getStreet());
        saved.setStreetNumber(dto.getStreetNumber());

        when(libraryRepository.findByName(dto.getName())).thenReturn(Optional.empty());
        when(libraryRepository.existsById(1)).thenReturn(false);
        when(libraryRepository.save(any(Library.class))).thenReturn(saved);

        LibraryDto result = libraryService.createLibrary(dto);
        assertNotNull(result.getId());
        assertEquals(dto.getName(), result.getName());
        verify(libraryRepository).save(any(Library.class));
    }
}

