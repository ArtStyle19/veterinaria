package com.vicgroup.veterinaria.modules.prediction.controller;

//package com.vicgroup.veterinaria.controller;

import com.vicgroup.veterinaria.modules.prediction.dto.request.PredictionRequest;
import com.vicgroup.veterinaria.modules.prediction.dto.response.PredictionResponse;
import com.vicgroup.veterinaria.modules.prediction.service.PredictionService;
import lombok.RequiredArgsConstructor; // For automatic constructor injection
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.; // Marks this class as a REST controller


import jakarta.validation.Valid; // For enabling validation on request body objects (requires Spring Boot Validation dependency)


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
