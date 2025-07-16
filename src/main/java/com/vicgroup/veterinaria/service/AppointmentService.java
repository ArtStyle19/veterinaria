package com.vicgroup.veterinaria.service;

import java.util.List;

import com.vicgroup.veterinaria.dto.*;
import com.vicgroup.veterinaria.model.*;
import com.vicgroup.veterinaria.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final VetProfileRepo vets;
    private final AppointmentRepo appointments;
    private final PetHistoricalRecordRepo histories;
    private final HistoricalRecordClinicRepo histClinics;

    private final SymptomRepo symptoms;


    // Only For VetOwner RN
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
