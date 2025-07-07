package com.vicgroup.veterinaria.controller;

import com.vicgroup.veterinaria.dto.*;
import com.vicgroup.veterinaria.service.AppointmentService;
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
import java.util.Map;


@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final PetService petService;
    private final AppointmentService appointmentService;


    @PostMapping("/{recordId}")
    @PreAuthorize("hasRole('VET')")
    public ResponseEntity<?> create(
            @PathVariable Long recordId,
            @RequestBody @Valid CreateAppointmentRequest request,
            @AuthenticationPrincipal User vetUser) {

        petService.createAppointment(recordId, request, vetUser);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Appointment created"
        ));
    }

    @GetMapping("/{recordId}")
    @PreAuthorize("hasRole('VET')")
    public ResponseEntity<List<AppointmentSummaryDto>> getAppointmentsByRecord(
            @PathVariable Long recordId,
            @AuthenticationPrincipal User vetUser
    ) {
        List<AppointmentSummaryDto> list = petService.getAppointmentsByRecord(recordId, vetUser);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/specific/{id}")
    @PreAuthorize("hasRole('VET')")
    public ResponseEntity<AppointmentDetailDto> getAppointment(@PathVariable Long id,
                                                               @AuthenticationPrincipal User vetUser) {
        AppointmentDetailDto dto = appointmentService.getAppointmentById(id, vetUser);
        return ResponseEntity.ok(dto);
    }
}
