package com.example.rlibrarybackend.controller;

import com.example.rlibrarybackend.api.PersonsApi;
import com.example.rlibrarybackend.dto.PersonDto;
import com.example.rlibrarybackend.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class PersonController implements PersonsApi {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public ResponseEntity<List<PersonDto>> personsGet(Integer page, Integer size) {
        int p = page != null ? page : 0;
        int s = size != null ? size : 20;
        log.info("GET /persons requested page={} size={}", p, s);
        List<PersonDto> persons = personService.getAllPersons(p, s);
        log.debug("GET /persons returned {} items", persons.size());
        return ResponseEntity.ok(persons);
    }

    @Override
    public ResponseEntity<PersonDto> personsIdGet(Long id) {
        log.info("GET /persons/{} requested", id);
        return personService.findPersonById(id)
                .map(dto -> {
                    log.debug("GET /persons/{} found", id);
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    log.warn("GET /persons/{} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Override
    public ResponseEntity<PersonDto> personsPost(PersonDto personDto) {
        log.info("POST /persons requested");
        PersonDto created = personService.createPerson(personDto);
        log.info("POST /persons created id={}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<Void> personsIdDelete(Long id) {
        log.info("DELETE /persons/{} requested", id);
        boolean deleted = personService.deletePerson(id);
        if (!deleted) {
            log.warn("DELETE /persons/{} not found", id);
            return ResponseEntity.notFound().build();
        }
        log.info("DELETE /persons/{} deleted", id);
        return ResponseEntity.noContent().build();
    }
}
