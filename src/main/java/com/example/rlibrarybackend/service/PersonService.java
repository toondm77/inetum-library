package com.example.rlibrarybackend.service;

import com.example.rlibrarybackend.dto.PersonDto;
import com.example.rlibrarybackend.model.AccountStatus;
import com.example.rlibrarybackend.model.Library;
import com.example.rlibrarybackend.model.Person;
import com.example.rlibrarybackend.repository.LibraryRepository;
import com.example.rlibrarybackend.repository.PersonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PersonService {
    private final PersonRepository personRepository;
    private final LibraryRepository libraryRepository;

    public PersonService(PersonRepository personRepository, LibraryRepository libraryRepository) {
        this.personRepository = personRepository;
        this.libraryRepository = libraryRepository;
    }

    public List<PersonDto> getAllPersons() {
        log.debug("Fetching all persons");
        List<PersonDto> persons = personRepository.findAll().stream()
                .map(this::mapEntityToDto)
                .toList();
        log.debug("Fetched {} persons", persons.size());
        return persons;
    }

    public Optional<PersonDto> findPersonById(Long id) {
        log.debug("Finding person id={}", id);
        return personRepository.findById(id.intValue()).map(this::mapEntityToDto);
    }

    public PersonDto createPerson(PersonDto dto) {
        // Validate birthdate must be before today
        if (dto.getBirthDate() != null && !dto.getBirthDate().isBefore(LocalDate.now())) {
            log.warn("Create person failed: birthDate must be before today");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Birth date must be before today");
        }

        // Validate library exists if provided
        Library activeLibrary = null;
        if (dto.getActiveLibraryId() != null) {
            log.debug("Validating active library id={}", dto.getActiveLibraryId());
            activeLibrary = libraryRepository.findById(dto.getActiveLibraryId().intValue())
                    .orElseThrow(() -> {
                        log.warn("Create person failed: library id={} not found", dto.getActiveLibraryId());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Library not found");
                    });
        }

        Person toSave = mapDtoToEntity(dto);
        toSave.setActiveLibrary(activeLibrary);
        Person saved = personRepository.save(toSave);
        log.info("Created person id={}", saved.getId());
        return mapEntityToDto(saved);
    }

    public boolean deletePerson(Long id) {
        Integer entityId = id.intValue();
        if (!personRepository.existsById(entityId)) {
            log.warn("Person id={} not found for delete", id);
            return false;
        }
        personRepository.deleteById(entityId);
        log.info("Deleted person id={}", id);
        return true;
    }

    private PersonDto mapEntityToDto(Person person) {
        PersonDto dto = new PersonDto();
        dto.setId(person.getId() != null ? person.getId().longValue() : null);
        dto.setFirstName(person.getFirstName());
        dto.setLastName(person.getLastName());
        dto.setBirthDate(person.getBirthDate());
        dto.setFunctie(person.getFunctie());
        dto.setBadgeCode(person.getBadgeCode());
        if (person.getActiveLibrary() != null && person.getActiveLibrary().getId() != null) {
            dto.setActiveLibraryId(person.getActiveLibrary().getId().longValue());
        }
        dto.setAccountStatus(person.getAccountStatus() != null ? person.getAccountStatus().name() : null);
        if (person.getPersonalStats() != null && person.getPersonalStats().getId() != null) {
            dto.setPersonalStatsId(person.getPersonalStats().getId().longValue());
        }
        if (person.getLoans() != null) {
            dto.setLoanIds(person.getLoans().stream()
                    .filter(loan -> loan.getId() != null)
                    .map(loan -> loan.getId().longValue())
                    .toList());
        }
        if (person.getBookComplaints() != null) {
            dto.setBookComplaintIds(person.getBookComplaints().stream()
                    .filter(complaint -> complaint.getId() != null)
                    .map(complaint -> complaint.getId().longValue())
                    .toList());
        }
        return dto;
    }

    private Person mapDtoToEntity(PersonDto dto) {
        Person person = new Person();
        if (dto.getId() != null) {
            person.setId(dto.getId().intValue());
        }
        person.setFirstName(dto.getFirstName());
        person.setLastName(dto.getLastName());
        person.setBirthDate(dto.getBirthDate());
        person.setFunctie(dto.getFunctie());
        person.setBadgeCode(dto.getBadgeCode());
        if (dto.getAccountStatus() != null) {
            try {
                person.setAccountStatus(AccountStatus.valueOf(dto.getAccountStatus().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                person.setAccountStatus(AccountStatus.ACTIVE);
            }
        } else {
            person.setAccountStatus(AccountStatus.ACTIVE);
        }
        person.setLoans(new ArrayList<>());
        person.setBookComplaints(new ArrayList<>());
        return person;
    }
}
