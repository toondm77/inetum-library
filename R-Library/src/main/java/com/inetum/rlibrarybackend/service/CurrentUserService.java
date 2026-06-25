package com.inetum.rlibrarybackend.service;

import com.inetum.rlibrarybackend.model.Person;
import com.inetum.rlibrarybackend.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Resolves the currently authenticated user by looking up their Person record
 * using the Auth0 "sub" claim from the JWT token.
 */
@Service
@Slf4j
public class CurrentUserService {

    private final PersonRepository personRepository;

    public CurrentUserService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person getCurrentPerson() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        String auth0Id = jwt.getSubject();
        log.debug("Resolving current person for auth0Id={}", auth0Id);

        return personRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> {
                    log.warn("No person found for auth0Id={}", auth0Id);
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user not found in system");
                });
    }

    public boolean currentUserIsStaff() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_VERANTWOORDELIJKE"));
    }
}

