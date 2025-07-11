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
public class PetService {

    private final PetRepo pets;
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


    public Pet createAsOwner(CreatePetRequest r, User user) {
        OwnerProfile owner = owners.findByUserId(user.getId()).orElseThrow();

        Pet pet = new Pet();
        pet.setName(r.name);
        pet.setSpecies(r.species);
        pet.setBreed(r.breed);
        pet.setSex(SexEnum.valueOf(r.sex));
        pet.setStatus(PetStatusEnum.valueOf(r.status));
        pet.setBirthdate(r.birthdate);

        // ✅ Establecer clínica por defecto si viene null
        pet.setHomeClinicId(r.homeClinicId != null ? r.homeClinicId : 1L);


        pet.setOwner(owner.getUser());
        pet.setEditCode(UUID.randomUUID().toString());
        pet.setVisibility(VisibilityEnum.CLINIC_ONLY); // o cualquier valor por defecto que desees

        return pets.save(pet);
    }


    public Pet createAsVet(CreatePetWithHistoryRequest r, User user) {
        VetProfile vet = vets.findByUserId(user.getId()).orElseThrow();

        Pet pet = new Pet();
        pet.setName(r.name);
        pet.setSpecies(r.species);
        pet.setBreed(r.breed);
        pet.setSex(SexEnum.valueOf(r.sex));
        pet.setStatus(PetStatusEnum.valueOf(r.status));
        pet.setBirthdate(r.birthdate);
        pet.setHomeClinicId(vet.getClinic().getId());
        pet.setOwnerName(r.ownerName);
        pet.setOwnerContact(r.ownerContact);
        pet.setEditCode(UUID.randomUUID().toString());

        Pet saved = pets.save(pet);

        // Historical record
        PetHistoricalRecord record = new PetHistoricalRecord();
        record.setPet(saved);
        record.setCreatedBy(user);
        histories.save(record);

        // Pet-clinic link
        if (!petClinics.existsByPetIdAndClinicId(saved.getId(), vet.getClinic().getId())) {
            PetClinic pc = new PetClinic();
            pc.setPetId(saved.getId());
            pc.setClinicId(vet.getClinic().getId());
            petClinics.save(pc);
        }

        // Historical record-clinic access
        if (!histClinics.existsByRecordIdAndClinicId(record.getId(), vet.getClinic().getId())) {
            HistoricalRecordClinic hc = new HistoricalRecordClinic();
            hc.setRecordId(record.getId());
            hc.setClinicId(vet.getClinic().getId());
            hc.setAccessLevel(AccessLevelEnum.FULL);
            histClinics.save(hc);
        }

        return saved;
    }


    public Pet importPet(ImportPetRequest r, User user) {
        UUID token = UUID.fromString(r.qrCodeToken);
        Pet pet = pets.findByQrCodeToken(token)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        // ✅ Validate edit code
        if (!pet.getEditCode().equals(r.editCode)) {
            throw new RuntimeException("Invalid edit code");
        }

        String role = user.getRole().getName();

        // === OWNER IMPORT LOGIC ===
        if (role.equals("PET_OWNER")) {
            // Allow only if pet has no owner yet
            if (pet.getOwner() != null) {
                throw new RuntimeException("Pet is already linked to an owner");
            }

            OwnerProfile owner = owners.findByUserId(user.getId()).orElseThrow();
            pet.setOwner(owner.getUser());
            return pets.save(pet);
        }

        // === VET IMPORT LOGIC ===
        if (role.equals("VET")) {
            VetProfile vet = vets.findByUserId(user.getId()).orElseThrow();
            Long clinicId = vet.getClinic().getId();

            // Link to clinic if not already
            if (!petClinics.existsByPetIdAndClinicId(pet.getId(), clinicId)) {
                PetClinic pc = new PetClinic();
                pc.setPetId(pet.getId());
                pc.setClinicId(clinicId);
                petClinics.save(pc);
            }

            PetHistoricalRecord record = histories.findByPet(pet).orElse(null);

            if (record == null) {
                // Create new record if missing (owner-created pet)
                record = new PetHistoricalRecord();
                record.setPet(pet);
                record.setCreatedBy(user); // vet user
                histories.save(record);
            }
///
            if (!histClinics.existsByRecordIdAndClinicId(record.getId(), clinicId)) {
                HistoricalRecordClinic hc = new HistoricalRecordClinic();
                hc.setRecordId(record.getId());
                hc.setClinicId(clinicId);
                hc.setAccessLevel(AccessLevelEnum.WRITE);
                histClinics.save(hc);
            }

            // Update home clinic to importing clinic
            pet.setHomeClinicId(clinicId);

            return pets.save(pet);
        }

        throw new RuntimeException("Role not authorized to import pet");
    }



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

        appointmentsRepo.save(a); // Persist first to get ID

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

        List<Appointment> list = appointmentsRepo.findByRecordId(recordId);

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


    // GET

    public PetDetailDto getPetDetails(Long petId) {
        Pet pet = pets.findById(petId).orElseThrow(() ->
                new RuntimeException("Pet not found"));

        return PetDetailDto.fromEntity(pet);
    }


    @Transactional
    public PublicPetDto getPublicPetByQrToken(UUID token) {
        Pet pet = pets.findByQrCodeToken(token)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        Clinic clinic = clinics.findById(pet.getHomeClinicId()).orElse(null);

        OwnerProfile owner = null;
        if (pet.getOwner() != null) {
            owner = owners.findByUserId(pet.getOwner().getId()).orElse(null);
        }

        return PublicPetDto.fromEntity(pet, clinic, owner);
    }



    public List<PetListItemDto> listPets(User user) {
        String role = user.getRole().getName();

        if (role.equals("PET_OWNER")) {
            OwnerProfile owner = owners.findByUserId(user.getId()).orElseThrow();
            return pets.findByOwner(owner.getUser()).stream()
                    .map(PetListItemDto::from)
                    .toList();
        }

        if (role.equals("VET")) {
            VetProfile vet = vets.findByUserId(user.getId()).orElseThrow();
            Long clinicId = vet.getClinic().getId();

            List<PetClinic> links = petClinics.findByClinicId(clinicId);
            return links.stream()
                    .map(link -> pets.findById(link.getPetId()).orElse(null))
                    .filter(Objects::nonNull)
                    .map(PetListItemDto::from)
                    .toList();
        }

        throw new RuntimeException("Unauthorized role");
    }


//
//    public List<HistoricalRecordDto> getFullHistoryForPet(Long petId) {
//        // Obtener todos los historiales de esa mascota (sin importar clínica)
//        List<PetHistoricalRecord> records = histories.findByPetId(petId);
//
//        return records.stream().map(record -> {
//            HistoricalRecordDto dto = new HistoricalRecordDto();
//            dto.setRecordId(record.getId());
//
////            // Obtener la clínica (si querés mostrarla)
////            HistoricalRecordClinic link = histClinics.findByRecordId(record.getId())
////                    .orElse(null);
////
////            if (link != null) {
////                dto.setClinic(ClinicDto.fromEntity(clinics.findById(link.getClinicId()).orElse(null)));
////            }
//
//            List<HistoricalRecordClinic> links = histClinics.findByRecordId(record.getId());
//
//            if (!links.isEmpty()) {
//    /*  puedes:
//        - tomar el primero
//        - filtrar por homeClinicId
//        - o devolver lista completa en el DTO
//       aquí escogemos el primero para no romper el front  */
//                Long clinicId = links.get(0).getClinicId();
//                clinics.findById(clinicId).ifPresent(c ->
//                        dto.setClinic(ClinicDto.fromEntity(c))
//                );
//            }
//
//
//            // Obtener las citas
//            List<Appointment> appointments = appointmentsRepo.findByRecordId(record.getId());
//
//            List<AppointmentSummaryDto> summaries = appointments.stream().map(app -> {
//                AppointmentSummaryDto summary = new AppointmentSummaryDto();
//                summary.setId(app.getId());
//                summary.setDate(app.getDate());
//                summary.setWeight(app.getWeight());
//                summary.setTemperature(app.getTemperature());
//                summary.setDiagnosis(app.getDiagnosis());
//                summary.setSymptoms(symptomRepo.findNamesByAppointmentId(app.getId()));
//                return summary;
//            }).toList();
//
//            dto.setAppointments(summaries);
//            return dto;
//        }).toList();
//    }

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







}