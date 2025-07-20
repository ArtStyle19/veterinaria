package com.vicgroup.veterinaria.modules.appointment.service;

import java.util.List;

import com.vicgroup.veterinaria.modules.appointment.dto.request.CreateAppointmentRequest;
import com.vicgroup.veterinaria.modules.appointment.dto.response.AppointmentDetailDto;
import com.vicgroup.veterinaria.modules.appointment.dto.shared.AppointmentSummaryDto;
import com.vicgroup.veterinaria.modules.appointment.model.Appointment;
import com.vicgroup.veterinaria.modules.appointment.model.AppointmentSymptom;
import com.vicgroup.veterinaria.modules.appointment.repository.AppointmentRepo;
import com.vicgroup.veterinaria.modules.appointment.repository.AppointmentSymptomRepo;
import com.vicgroup.veterinaria.modules.pet.model.PetHistoricalRecord;
import com.vicgroup.veterinaria.modules.pet.repository.PetHistoricalRecordRepo;
import com.vicgroup.veterinaria.modules.record.repository.HistoricalRecordClinicRepo;
import com.vicgroup.veterinaria.modules.symptom.model.Symptom;
import com.vicgroup.veterinaria.modules.symptom.repository.SymptomRepo;
import com.vicgroup.veterinaria.modules.user.model.User;
import com.vicgroup.veterinaria.modules.user.model.VetProfile;
import com.vicgroup.veterinaria.modules.user.repository.VetProfileRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final VetProfileRepo vets;
    private final AppointmentRepo appointmentRepo;
    private final PetHistoricalRecordRepo histories;
    private final HistoricalRecordClinicRepo histClinics;
    private final AppointmentSymptomRepo appointmentSymptoms;

    private final SymptomRepo symptoms;


    // Only For Vet RN
    public AppointmentDetailDto getAppointmentById(Long appointmentId, User vetUser) {
        VetProfile vet = vets.findByUserId(vetUser.getId()).orElseThrow();

        Appointment a = appointmentRepo.findById(appointmentId)
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

    @Transactional
    public void createAppointment(Long recordId, CreateAppointmentRequest req, User vetUser) {
        VetProfile vet = vets.findByUserId(vetUser.getId()).orElseThrow();

        // ✅ Ensure vet has WRITE access to this record
        if (!histClinics.existsByRecordIdAndClinicId(recordId, vet.getClinic().getId())) {
            throw new RuntimeException("Vet not authorized to write on this record");
        }

        // ✅ Create appointment
        Appointment a = new Appointment();
        a.setRecordId(recordId);
        a.setDate(req.getDate());
        a.setWeight(req.getWeight());
        a.setTemperature(req.getTemperature());
        a.setHeartRate(req.getHeartRate());
        a.setDescription(req.getDescription());
        a.setTreatments(req.getTreatments());
        a.setDiagnosis(req.getDiagnosis());
        a.setNotes(req.getNotes());
        a.setCreatedBy(vetUser);

        appointmentRepo.save(a); // Persist first to get ID

        // ✅ Handle symptoms (create if missing, then link)
        if (req.getSymptoms() != null) {
            for (String name : req.getSymptoms()) {
                Symptom s = symptoms.findByName(name)
                        .orElseGet(() -> {
                            Symptom newSymptom = new Symptom();
                            newSymptom.setName(name);
                            return symptoms.save(newSymptom);
                        });

                AppointmentSymptom link = new AppointmentSymptom();
                link.setAppointmentId(a.getId());
                link.setSymptomId(s.getId());
                appointmentSymptoms.save(link);
            }
        }
        // ✅ Optional: Audit log
        // accessLogRepo.save(new AccessLog(vetUser.getId(), recordId, LogActionEnum.EDIT));
    }


    public List<AppointmentSummaryDto> getAppointmentsByRecord(Long recordId, User vetUser) {
        VetProfile vet = vets.findByUserId(vetUser.getId()).orElseThrow();

        // ✅ Verificar acceso
        if (!histClinics.existsByRecordIdAndClinicId(recordId, vet.getClinic().getId())) {
            throw new RuntimeException("Vet not authorized to view this record");
        }

        List<Appointment> list = appointmentRepo.findByRecordId(recordId);

        return list.stream().map(app -> {
            AppointmentSummaryDto dto = new AppointmentSummaryDto();
            dto.setId(app.getId());
            dto.setDate(app.getDate());
            dto.setWeight(app.getWeight());
            dto.setTemperature(app.getTemperature());
            dto.setHeartRate(app.getHeartRate());
            dto.setDescription(app.getDescription());
            dto.setTreatments(app.getTreatments());
            dto.setDiagnosis(app.getDiagnosis());
            dto.setNotes(app.getNotes());
            dto.setCreatedById(app.getCreatedBy().getId());

            // ✅ Cargar síntomas
            List<String> symps = symptoms.findNamesByAppointmentId(app.getId());
            dto.setSymptoms(symps);

            return dto;
        }).toList();
    }


}
