package com.vicgroup.veterinaria.service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.vicgroup.veterinaria.dto.*;
import com.vicgroup.veterinaria.model.*;
import com.vicgroup.veterinaria.model.enums.PetStatusEnum;
import com.vicgroup.veterinaria.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.vicgroup.veterinaria.model.enums.SexEnum;
import com.vicgroup.veterinaria.model.enums.AccessLevelEnum;

import com.vicgroup.veterinaria.model.enums.VisibilityEnum;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final VetProfileRepo vets;
    private final AppointmentRepo appointments;
    private final PetHistoricalRecordRepo histories;
    private final HistoricalRecordClinicRepo histClinics;

    private final SymptomRepo symptoms;


    public AppointmentDetailDto getAppointmentById(Long appointmentId, User vetUser) {
        VetProfile vet = vets.findByUserId(vetUser.getId()).orElseThrow();

        Appointment a = appointments.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // ✅ Validar acceso a la clínica
        PetHistoricalRecord record = histories.findById(a.getRecordId())
                .orElseThrow(() -> new RuntimeException("Record not found"));

        boolean hasAccess = histClinics.existsByRecordIdAndClinicId(record.getId(), vet.getClinic().getId());

        if (!hasAccess) {
            throw new RuntimeException("Vet not authorized to view this record");
        }

        AppointmentDetailDto dto = new AppointmentDetailDto();
        dto.setId(a.getId());
        dto.setDate(a.getDate());
        dto.setWeight(a.getWeight());
        dto.setTemperature(a.getTemperature());
        dto.setHeartRate(a.getHeartRate());
        dto.setDescription(a.getDescription());
        dto.setTreatments(a.getTreatments());
        dto.setDiagnosis(a.getDiagnosis());
        dto.setNotes(a.getNotes());
        dto.setCreatedById(a.getCreatedBy().getId());

        // ✅ síntomas
        List<String> symptomsList = symptoms.findNamesByAppointmentId(a.getId());
        dto.setSymptoms(symptomsList);

        return dto;
    }
    ///


}
