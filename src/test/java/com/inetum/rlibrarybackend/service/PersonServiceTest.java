package com.inetum.rlibrarybackend.service;

import com.inetum.rlibrarybackend.dto.PersonDto;
import com.inetum.rlibrarybackend.model.AccountStatus;
import com.inetum.rlibrarybackend.model.Library;
import com.inetum.rlibrarybackend.model.Person;
import com.inetum.rlibrarybackend.repository.LibraryRepository;
import com.inetum.rlibrarybackend.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private LibraryRepository libraryRepository;

    @InjectMocks
    private PersonService personService;

    private PersonDto dto;

    @BeforeEach
    void setUp() {
        dto = new PersonDto();
        dto.setFirstName("Alice");
        dto.setLastName("Smith");
        dto.setBirthDate(LocalDate.now().minusYears(20));
        dto.setAccountStatus(AccountStatus.ACTIVE.name());
    }

    @Test
    void createPerson_throwsBadRequest_whenBirthDateNotBeforeToday() {
        dto.setBirthDate(LocalDate.now());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> personService.createPerson(dto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    void createPerson_throwsNotFound_whenLibraryMissing() {
        dto.setActiveLibraryId(99L);
        when(libraryRepository.findById(99)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> personService.createPerson(dto));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void createPerson_succeeds_andReturnsDto() {
        dto.setActiveLibraryId(null);
        Person saved = new Person();
        saved.setId(5);
        saved.setFirstName(dto.getFirstName());
        saved.setLastName(dto.getLastName());
        saved.setBirthDate(dto.getBirthDate());
        saved.setAccountStatus(AccountStatus.ACTIVE);

        when(personRepository.save(any(Person.class))).thenReturn(saved);

        PersonDto result = personService.createPerson(dto);
        assertEquals(5L, result.getId());
        assertEquals(dto.getFirstName(), result.getFirstName());
        assertEquals(dto.getBirthDate(), result.getBirthDate());
        verify(personRepository).save(any(Person.class));
    }

    @Test
    void createPerson_succeeds_whenLibraryExists() {
        dto.setActiveLibraryId(1L);
        Library library = new Library();
        library.setId(1);
        when(libraryRepository.findById(1)).thenReturn(Optional.of(library));

        Person saved = new Person();
        saved.setId(10);
        saved.setFirstName(dto.getFirstName());
        saved.setLastName(dto.getLastName());
        saved.setBirthDate(dto.getBirthDate());
        saved.setAccountStatus(AccountStatus.ACTIVE);
        saved.setActiveLibrary(library);

        when(personRepository.save(any(Person.class))).thenReturn(saved);

        PersonDto result = personService.createPerson(dto);
        assertEquals(10L, result.getId());
        assertEquals(1L, result.getActiveLibraryId());
        verify(libraryRepository).findById(1);
        verify(personRepository).save(any(Person.class));
    }
}

