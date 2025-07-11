package com.vicgroup.veterinaria.controller;

//package com.vicgroup.veterinaria.controller;

import com.vicgroup.veterinaria.dto.*;
import com.vicgroup.veterinaria.service.PredictionService;
import lombok.RequiredArgsConstructor; // For automatic constructor injection
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.; // Marks this class as a REST controller


import jakarta.validation.Valid; // For enabling validation on request body objects (requires Spring Boot Validation dependency)

import com.vicgroup.veterinaria.model.Pet;
import com.vicgroup.veterinaria.model.User;
import com.vicgroup.veterinaria.service.PetService;

import java.util.List;


@RestController
@RequestMapping("/api/predictions")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService service;

    // Solo veterinarios
    @PreAuthorize("hasRole('VET')")
    @PostMapping
    public PredictionResponse predict(@RequestBody @Valid PredictionRequest r) {
        return service.predict(r.symptoms());
    }
}
