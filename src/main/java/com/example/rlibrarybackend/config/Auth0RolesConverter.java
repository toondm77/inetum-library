package com.example.rlibrarybackend.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
 * It also preserves standard scope-based authorities (SCOPE_...) from the
 * default JwtGrantedAuthoritiesConverter so no JWT claims are lost.
 *
 * Example: role "Verantwoordelijke" → ROLE_VERANTWOORDELIJKE
 *          role "Werknemer"         → ROLE_WERKNEMER
 */
public class Auth0RolesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String ROLES_CLAIM = "https://rlibrary.com/roles";

    private final JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        var authorities = new ArrayList<GrantedAuthority>(defaultConverter.convert(jwt));

        List<?> roles = jwt.getClaimAsStringList(ROLES_CLAIM);

        if (roles != null) {
            roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase()))
                    .forEach(authorities::add);
        }

        return authorities;
    }
}

