package com.vicgroup.veterinaria.modules.record.service;

import com.vicgroup.veterinaria.core.enums.AccessLevelEnum;
import com.vicgroup.veterinaria.modules.appointment.dto.shared.AppointmentSummaryDto;
import com.vicgroup.veterinaria.modules.appointment.model.Appointment;
import com.vicgroup.veterinaria.modules.appointment.repository.AppointmentRepo;
import com.vicgroup.veterinaria.modules.appointment.repository.AppointmentSymptomRepo;
import com.vicgroup.veterinaria.modules.clinic.dto.shared.ClinicDto;
import com.vicgroup.veterinaria.modules.clinic.model.Clinic;
import com.vicgroup.veterinaria.modules.clinic.repository.ClinicRepo;
import com.vicgroup.veterinaria.modules.pet.model.Pet;
import com.vicgroup.veterinaria.modules.pet.model.PetHistoricalRecord;
import com.vicgroup.veterinaria.modules.pet.repository.PetClinicRepo;
import com.vicgroup.veterinaria.modules.pet.repository.PetHistoricalRecordRepo;
import com.vicgroup.veterinaria.modules.pet.repository.PetRepo;
import com.vicgroup.veterinaria.modules.record.dto.request.UpdateAccessLevelRequest;
import com.vicgroup.veterinaria.modules.record.dto.response.HistoricalRecordDto;
import com.vicgroup.veterinaria.modules.record.dto.response.RecordAccessLevelResponse;
import com.vicgroup.veterinaria.modules.record.model.HistoricalRecordClinic;
import com.vicgroup.veterinaria.modules.record.repository.HistoricalRecordClinicRepo;
import com.vicgroup.veterinaria.modules.symptom.repository.SymptomRepo;
import com.vicgroup.veterinaria.modules.user.repository.OwnerProfileRepo;
import com.vicgroup.veterinaria.modules.user.repository.VetProfileRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class RecordService {
    private final PetRepo petRepo;
    private final PetHistoricalRecordRepo histories;
    private final PetClinicRepo petClinics;
    private final HistoricalRecordClinicRepo histClinics;
    private final VetProfileRepo vets;
    private final OwnerProfileRepo owners;

    private final AppointmentRepo appointmentsRepo;
    private final SymptomRepo symptoms;

    private final AppointmentSymptomRepo appointmentSymptoms;
    private final SymptomRepo symptomRepo;
    private final ClinicRepo clinics;

    public List<HistoricalRecordDto> getFullHistoryForPet(Long petId) {
        List<PetHistoricalRecord> records = histories.findByPetId(petId);

        return records.stream().map(record -> {
            HistoricalRecordDto dto = new HistoricalRecordDto();
            dto.setRecordId(record.getId());

            /* 1. TODAS las clínicas con acceso */
            List<ClinicDto> clinicDtos = histClinics.findByRecordId(record.getId()).stream()
                    .map(link -> clinics.findById(link.getClinicId())
                            .map(ClinicDto::fromEntity)
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .toList();
            dto.setClinics(clinicDtos);

            /* 2. Citas + nombre de clínica */
            List<Appointment> appointments = appointmentsRepo.findByRecordId(record.getId());

            List<AppointmentSummaryDto> summaries = appointments.stream().map(app -> {
                AppointmentSummaryDto summary = new AppointmentSummaryDto();
                summary.setId(app.getId());
                summary.setDate(app.getDate());
                summary.setWeight(app.getWeight());
                summary.setTemperature(app.getTemperature());
                summary.setHeartRate(app.getHeartRate());
                summary.setDescription(app.getDescription());
                summary.setTreatments(app.getTreatments());
                summary.setDiagnosis(app.getDiagnosis());
                summary.setNotes(app.getNotes());
                summary.setCreatedById(app.getCreatedBy().getId());
                summary.setSymptoms(symptomRepo.findNamesByAppointmentId(app.getId()));

                /* 🔎 clínica responsable = clínica del vet creador */
                Long clinicId = vets.findClinicIdByUserId(app.getCreatedBy().getId())
                        .orElse(null);
                summary.setClinicId(clinicId);
                if (clinicId != null) {
                    clinics.findById(clinicId)
                            .ifPresent(c -> summary.setClinicName(c.getName()));
                }
                return summary;
            }).toList();

            dto.setAppointments(summaries);
            return dto;
        }).toList();
    }

    public List<RecordAccessLevelResponse> getAccessesForPet(Long petId, Long ownerId) {
        Pet pet = petRepo.findByIdAndOwner_Id(petId, ownerId)
                .orElseThrow(() -> new AccessDeniedException("You do not own this pet"));

        List<PetHistoricalRecord> records = histories.findByPetId(petId);
        if (records.isEmpty()) {
            throw new IllegalStateException("No record found for this pet");
        }

        List<RecordAccessLevelResponse> result = new ArrayList<>();

        for (PetHistoricalRecord record : records) {
            List<HistoricalRecordClinic> accesses = histClinics.findByRecordId(record.getId());

            for (HistoricalRecordClinic access : accesses) {
                Clinic clinic = clinics.findById(access.getClinicId())
                        .orElseThrow(() -> new EntityNotFoundException("Clinic not found"));

                result.add(new RecordAccessLevelResponse(
                        clinic.getId(),
                        clinic.getName(),
                        access.getAccessLevel().name()
                ));
            }
        }

        return result;
    }

    public void updateAccessLevelForClinic(Long petId, Long ownerId, UpdateAccessLevelRequest request) {
        // 1. Verificar que la mascota le pertenezca al usuario
        Pet pet = petRepo.findByIdAndOwner_Id(petId, ownerId)
                .orElseThrow(() -> new AccessDeniedException("You do not own this pet"));

        // 2. Obtener el historial médico (asumimos 1 por mascota, si hay más, ajustar)
        PetHistoricalRecord record = histories.findByPetId(petId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No historical record found"));

        // 3. Buscar el vínculo entre la clínica y el historial
        HistoricalRecordClinic link = histClinics.findByRecordIdAndClinicId(record.getId(), request.getClinicId())
                .orElseThrow(() -> new EntityNotFoundException("This clinic does not have access"));

        // 4. Cambiar el nivel de acceso
        link.setAccessLevel(AccessLevelEnum.valueOf(request.getAccessLevel()));

        // 5. Guardar cambios
        histClinics.save(link);
    }


}
