package com.vicgroup.veterinaria.modules.symptom.controller;

import com.vicgroup.veterinaria.modules.symptom.repository.SymptomRepo;
import lombok.RequiredArgsConstructor; // For automatic constructor injection
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.; // Marks this class as a REST controller




import java.util.List;


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