package com.example.rlibrarybackend.controller;

import com.example.rlibrarybackend.api.PersonsApi;
import com.example.rlibrarybackend.dto.PersonDto;
import com.example.rlibrarybackend.service.PersonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PersonController implements PersonsApi {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public ResponseEntity<List<PersonDto>> personsGet() {
        return ResponseEntity.ok(personService.getAllPersons());
    }

    @Override
    public ResponseEntity<PersonDto> personsIdGet(Long id) {
        return personService.findPersonById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<PersonDto> personsPost(PersonDto personDto) {
        PersonDto created = personService.createPerson(personDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<Void> personsIdDelete(Long id) {
        boolean deleted = personService.deletePerson(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}

