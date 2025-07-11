package com.vicgroup.veterinaria.controller;

import com.vicgroup.veterinaria.dto.*;
import com.vicgroup.veterinaria.repository.SymptomRepo;
import com.vicgroup.veterinaria.service.AppointmentService;
import lombok.RequiredArgsConstructor; // For automatic constructor injection
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.; // Marks this class as a REST controller




import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/symptoms")
@RequiredArgsConstructor
public class SymptomController {

    private final SymptomRepo repo;

    @GetMapping
    public ResponseEntity<List<String>> getAllSymptomNames() {
        List<String> names = repo.findAllNames();
        return ResponseEntity.ok(names);
    }
}