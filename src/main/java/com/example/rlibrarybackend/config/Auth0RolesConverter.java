package com.example.rlibrarybackend.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Converts Auth0 JWT claims into Spring Security GrantedAuthority objects.
 *
 * Auth0 does NOT include roles in the JWT by default. You must add an Action
 *
 *   exports.onExecutePostLogin = async (event, api) => {
 *     const namespace = 'https://rlibrary.com/';
 *     const roles = event.authorization?.roles ?? [];
 *     api.idToken.setCustomClaim(`${namespace}roles`, roles);
 *     api.accessToken.setCustomClaim(`${namespace}roles`, roles);
 *   };
 *
 * This converter reads the claim "https://rlibrary.com/roles" and maps each
 * role to a ROLE_<NAME> Spring Security authority (uppercased).
 *
 * Example: role "Verantwoordelijke" → ROLE_VERANTWOORDELIJKE
 *          role "Werknemer"         → ROLE_WERKNEMER
 */
@Component
public class Auth0RolesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String ROLES_CLAIM = "https://rlibrary.com/roles";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<?> roles = jwt.getClaimAsStringList(ROLES_CLAIM);

        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }

        return roles.stream()
                .filter(role -> role instanceof String)
                .map(role -> (String) role)
                .flatMap(role -> Stream.of(
                        new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())
                ))
                .collect(java.util.stream.Collectors.toList());
    }
}

