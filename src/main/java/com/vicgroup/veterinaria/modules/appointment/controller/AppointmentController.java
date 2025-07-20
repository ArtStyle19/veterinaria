package com.vicgroup.veterinaria.modules.appointment.controller;

import com.vicgroup.veterinaria.modules.appointment.dto.response.AppointmentDetailDto;
import com.vicgroup.veterinaria.modules.appointment.dto.shared.AppointmentSummaryDto;
import com.vicgroup.veterinaria.modules.appointment.dto.request.CreateAppointmentRequest;
import com.vicgroup.veterinaria.modules.appointment.service.AppointmentService;
import lombok.RequiredArgsConstructor; // For automatic constructor injection
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.; // Marks this class as a REST controller


import jakarta.validation.Valid; // For enabling validation on request body objects (requires Spring Boot Validation dependency)

import com.vicgroup.veterinaria.modules.user.model.User;
import com.vicgroup.veterinaria.modules.pet.service.PetService;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/appointment/")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    // 1 Create Appo in a record
    //
    // http://localhost:8080/api/appointments/12
    @PostMapping("/{recordId}/create")
    @PreAuthorize("hasRole('VET')")
    public ResponseEntity<?> create(
            @PathVariable Long recordId,
            @RequestBody @Valid CreateAppointmentRequest request,
            @AuthenticationPrincipal User vetUser) {

        appointmentService.createAppointment(recordId, request, vetUser);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Appointment created"
        ));
    }

    // 2 Get All Appos in a record
    // Only If vet is in Intermediate table Historical Record Clinic
    // Add Owner , cause now Only vet has access
//    @GetMapping("/{recordId}")
//    @PreAuthorize("hasRole('VET')")
//    public ResponseEntity<List<AppointmentSummaryDto>> getAppointmentsByRecord(
//            @PathVariable Long recordId,
//            @AuthenticationPrincipal User vetUser
//    ) {
//        List<AppointmentSummaryDto> list = petService.getAppointmentsByRecord(recordId, vetUser);
//        return ResponseEntity.ok(list);
//    }



    // 3
    // Get Appo By Id but with nulls in clinic
    // http://localhost:8080/api/appointments/specific/1342 number of cite
    //    {
    //        "id": 1342,
    //            "date": "2025-07-15T16:05:15.568Z",
    //            "weight": 123.00,
    //            "temperature": 123.0,
    //            "heartRate": 123,
    //            "description": "Descripcion",
    //            "treatments": "trat",
    //            "diagnosis": "Diagnosis",
    //            "notes": "Notas",
    //            "createdById": 2,
    //            "symptoms": [],
    //        "clinicId": null,
    //            "clinicName": null
    //    }
    //  Allow Owner to see that
    //  Also try to call this by the record --- not only  id
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PET_OWNER', 'VET')")
    public ResponseEntity<AppointmentDetailDto> getAppointment(@PathVariable Long id,
                                                               @AuthenticationPrincipal User vetUser) {
        AppointmentDetailDto dto = appointmentService.getAppointmentById(id, vetUser);
        return ResponseEntity.ok(dto);
    }
}
