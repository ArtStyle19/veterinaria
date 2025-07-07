package com.vicgroup.veterinaria.service;

import org.springframework.stereotype.Service; // Marks this class as a Spring service
import lombok.RequiredArgsConstructor; // For automatic constructor injection

import org.springframework.transaction.annotation.Transactional; // For transactional methods

import com.vicgroup.veterinaria.repository.ClinicRepo;
import com.vicgroup.veterinaria.dto.ClinicDto;
import com.vicgroup.veterinaria.model.Clinic;


// src/main/java/com/centralvet/service/ClinicService.java
@Service
@RequiredArgsConstructor
public class ClinicService {

    private final ClinicRepo clinics;

    public ClinicDto create(ClinicDto dto) {
        Clinic c = new Clinic();
        dto.toEntity(c);
        clinics.save(c);
        return ClinicDto.fromEntity(c);
    }
}
