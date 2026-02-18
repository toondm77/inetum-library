package com.example.rlibrarybackend.service;

import com.example.rlibrarybackend.dto.PersonDto;
import com.example.rlibrarybackend.model.AccountStatus;
import com.example.rlibrarybackend.model.Person;
import com.example.rlibrarybackend.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<PersonDto> getAllPersons() {
        return personRepository.findAll().stream()
                .map(this::mapEntityToDto)
                .toList();
    }

    public Optional<PersonDto> findPersonById(Long id) {
        return personRepository.findById(id.intValue()).map(this::mapEntityToDto);
    }

    public PersonDto createPerson(PersonDto dto) {
        Person toSave = mapDtoToEntity(dto);
        Person saved = personRepository.save(toSave);
        return mapEntityToDto(saved);
    }

    public boolean deletePerson(Long id) {
        Integer entityId = id.intValue();
        if (!personRepository.existsById(entityId)) {
            return false;
        }
        personRepository.deleteById(entityId);
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

