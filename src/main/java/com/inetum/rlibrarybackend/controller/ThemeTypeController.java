package com.inetum.rlibrarybackend.controller;

import com.inetum.rlibrarybackend.api.ThemeTypesApi;
import com.inetum.rlibrarybackend.dto.ThemeType;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ThemeTypeController implements ThemeTypesApi {

    @Override
    @PreAuthorize("hasAnyRole('WERKNEMER', 'VERANTWOORDELIJKE')")
    public ResponseEntity<List<ThemeType>> themeTypesGet() {
        List<ThemeType> themeTypes = Arrays.stream(ThemeType.values())
                .map(themeType -> ThemeType.fromValue(themeType.name()))
                .toList();

        return ResponseEntity.ok(themeTypes);
    }
}
