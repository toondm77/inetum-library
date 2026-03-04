package com.inetum.rlibrarybackend.controller;

import com.inetum.rlibrarybackend.api.PersonsApi;
import com.inetum.rlibrarybackend.dto.PersonDto;
import com.inetum.rlibrarybackend.dto.PagedPersonResponse;
import com.inetum.rlibrarybackend.service.CurrentUserService;
import com.inetum.rlibrarybackend.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
@Slf4j
public class PersonController implements PersonsApi {

    private final PersonService personService;
    private final CurrentUserService currentUserService;

    public PersonController(PersonService personService, CurrentUserService currentUserService) {
        this.personService = personService;
        this.currentUserService = currentUserService;
    }

    @Override
    @PreAuthorize("hasAnyRole('WERKNEMER', 'VERANTWOORDELIJKE')")
    public ResponseEntity<PagedPersonResponse> personsGet(Integer page, Integer size, String sort, String direction, String lastName, String accountStatus) {
        // Regular users cannot list all persons
        if (!currentUserService.currentUserIsStaff()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own data");
        }
        int p = page != null ? page : 0;
        int s = size != null ? size : 20;
        String sortField = (sort != null && !sort.isBlank()) ? sort : "lastName";
        String dir = (direction != null && !direction.isBlank()) ? direction : "asc";
        log.info("GET /persons requested page={} size={} sort={} direction={}", p, s, sortField, dir);
        Page<PersonDto> personsPage = personService.getAllPersons(p, s, sortField, dir, lastName, accountStatus);
        PagedPersonResponse response = new PagedPersonResponse(
                personsPage.getNumber(),
                personsPage.getSize(),
                personsPage.getTotalElements(),
                personsPage.getTotalPages(),
                personsPage.getContent()
        );
        log.debug("GET /persons returned {} items", personsPage.getContent().size());
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasAnyRole('WERKNEMER', 'VERANTWOORDELIJKE')")
    public ResponseEntity<PersonDto> personsIdGet(Long id) {
        log.info("GET /persons/{} requested", id);
        // Regular users can only fetch their own record
        if (!currentUserService.currentUserIsStaff()) {
            var currentPerson = currentUserService.getCurrentPerson();
            if (!Long.valueOf(currentPerson.getId()).equals(id)) {
                log.warn("Access denied: personId={} tried to access personId={}", currentPerson.getId(), id);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own data");
            }
        }
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
    @PreAuthorize("hasRole('VERANTWOORDELIJKE')")
    public ResponseEntity<PersonDto> personsPost(PersonDto personDto) {
        log.info("POST /persons requested");
        PersonDto created = personService.createPerson(personDto);
        log.info("POST /persons created id={}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    @PreAuthorize("hasRole('VERANTWOORDELIJKE')")
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
