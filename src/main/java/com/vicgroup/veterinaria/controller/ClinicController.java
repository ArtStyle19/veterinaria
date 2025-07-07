package com.vicgroup.veterinaria.controller;


import com.vicgroup.veterinaria.model.User;
import com.vicgroup.veterinaria.model.VetProfile;
import com.vicgroup.veterinaria.repository.ClinicRepo;
import com.vicgroup.veterinaria.repository.VetProfileRepo;
import lombok.RequiredArgsConstructor; // For automatic constructor injection
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.; // Marks this class as a REST controller


import jakarta.validation.Valid; // For enabling validation on request body objects (requires Spring Boot Validation dependency)

import com.vicgroup.veterinaria.model.Clinic;
import com.vicgroup.veterinaria.service.ClinicService;
import com.vicgroup.veterinaria.dto.ClinicDto;

import java.util.List;


// src/main/java/com/centralvet/controller/ClinicController.java
@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
public class ClinicController {

    private final ClinicService clinicService;
    private final ClinicRepo clinicRepo;
    private final VetProfileRepo vetRepo;
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClinicDto> create(@Valid @RequestBody ClinicDto dto) {
        ClinicDto saved = clinicService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }

    @GetMapping
    public List<ClinicDto> getAllClinics() {
        return clinicRepo.findAll().stream()
                .map(ClinicDto::fromEntity)
                .toList();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('VET')")
    public ResponseEntity<ClinicDto> getMyClinic(@AuthenticationPrincipal User vetUser) {
        VetProfile profile = vetRepo.findByUserId(vetUser.getId())
                .orElseThrow(() -> new RuntimeException("Vet profile not found"));

        Clinic clinic = profile.getClinic();
        return ResponseEntity.ok(ClinicDto.fromEntity(clinic));
    }


}
