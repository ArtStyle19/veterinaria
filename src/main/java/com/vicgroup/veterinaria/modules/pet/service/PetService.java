package com.vicgroup.veterinaria.modules.pet.service;

import java.util.*;

import com.vicgroup.veterinaria.core.enums.PetStatusEnum;
import com.vicgroup.veterinaria.core.exceptions.NotFoundException;
import com.vicgroup.veterinaria.modules.appointment.dto.shared.AppointmentSummaryDto;
import com.vicgroup.veterinaria.modules.appointment.dto.request.CreateAppointmentRequest;
import com.vicgroup.veterinaria.modules.appointment.model.Appointment;
import com.vicgroup.veterinaria.modules.appointment.model.AppointmentSymptom;
import com.vicgroup.veterinaria.modules.appointment.repository.AppointmentRepo;
import com.vicgroup.veterinaria.modules.appointment.repository.AppointmentSymptomRepo;
import com.vicgroup.veterinaria.modules.clinic.dto.shared.ClinicDto;
import com.vicgroup.veterinaria.modules.clinic.model.Clinic;
import com.vicgroup.veterinaria.modules.clinic.repository.ClinicRepo;
import com.vicgroup.veterinaria.modules.pet.dto._public.PublicPetDto;
import com.vicgroup.veterinaria.modules.pet.dto.request.CreatePetRequest;
import com.vicgroup.veterinaria.modules.pet.dto.request.CreatePetWithHistoryRequest;
import com.vicgroup.veterinaria.modules.pet.dto.request.ImportPetRequest;
import com.vicgroup.veterinaria.modules.pet.dto.request.UpdatePetRequest;
import com.vicgroup.veterinaria.modules.pet.dto.response.EditCodeResponse;
import com.vicgroup.veterinaria.modules.pet.dto.response.PetDetailDto;
import com.vicgroup.veterinaria.modules.pet.dto.shared.PetListItemDto;
import com.vicgroup.veterinaria.modules.pet.model.Pet;
import com.vicgroup.veterinaria.modules.pet.model.PetClinic;
import com.vicgroup.veterinaria.modules.pet.model.PetHistoricalRecord;
import com.vicgroup.veterinaria.modules.pet.repository.PetClinicRepo;
import com.vicgroup.veterinaria.modules.pet.repository.PetHistoricalRecordRepo;
import com.vicgroup.veterinaria.modules.pet.repository.PetRepo;
import com.vicgroup.veterinaria.modules.record.dto.response.HistoricalRecordDto;
import com.vicgroup.veterinaria.modules.record.dto.response.RecordAccessLevelResponse;
import com.vicgroup.veterinaria.modules.record.model.HistoricalRecordClinic;
import com.vicgroup.veterinaria.modules.record.repository.HistoricalRecordClinicRepo;
import com.vicgroup.veterinaria.modules.symptom.model.Symptom;
import com.vicgroup.veterinaria.modules.symptom.repository.SymptomRepo;
import com.vicgroup.veterinaria.modules.user.dto.response.OwnerDetailDto;
import com.vicgroup.veterinaria.modules.user.model.OwnerProfile;
import com.vicgroup.veterinaria.modules.user.model.User;
import com.vicgroup.veterinaria.modules.user.model.VetProfile;
import com.vicgroup.veterinaria.modules.user.repository.OwnerProfileRepo;
import com.vicgroup.veterinaria.modules.user.repository.VetProfileRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.vicgroup.veterinaria.core.enums.SexEnum;
import com.vicgroup.veterinaria.core.enums.AccessLevelEnum;

import com.vicgroup.veterinaria.core.enums.VisibilityEnum;


@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepo petRepo;
    private final PetHistoricalRecordRepo histories;
    private final PetClinicRepo petClinics;
    private final HistoricalRecordClinicRepo histClinics;
    private final VetProfileRepo vetRepo;
    private final OwnerProfileRepo owners;

    private final AppointmentRepo appointmentsRepo;
    private final SymptomRepo symptoms;

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

        return petRepo.save(pet);
    }


    public Pet createAsVet(CreatePetWithHistoryRequest r, User user) {
        VetProfile vet = vetRepo.findByUserId(user.getId()).orElseThrow();

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

        Pet saved = petRepo.save(pet);

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
        Pet pet = petRepo.findByQrCodeToken(token)
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
            return petRepo.save(pet);
        }

        // === VET IMPORT LOGIC ===
        if (role.equals("VET")) {
            VetProfile vet = vetRepo.findByUserId(user.getId()).orElseThrow();
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

            return petRepo.save(pet);
        }

        throw new RuntimeException("Role not authorized to import pet");
    }


    // GET

//    public PetDetailDto getPetDetails(Long petId) {
//        Pet pet = pets.findById(petId).orElseThrow(() ->
//                new RuntimeException("Pet not found"));
//
//        return PetDetailDto.fromEntity(pet);
//    }

    public PetDetailDto getPetDetails(Long petId, User user) {
        Pet pet = petRepo.findById(petId).orElseThrow(() -> new RuntimeException("Pet not found"));

        if (user.getRole().getName().equals("VET")) {
            VetProfile vet = vetRepo.findByUserId(user.getId()).orElseThrow();
            PetHistoricalRecord record = histories.findByPet(pet).orElse(null);

            if (record == null) {
                return PetDetailDto.fromEntity(pet, user, Optional.empty());
            }

            Optional<HistoricalRecordClinic> access = histClinics.findByRecordIdAndClinicId(
                    record.getId(), vet.getClinic().getId()
            );

            return PetDetailDto.fromEntity(pet, user, access.map(HistoricalRecordClinic::getAccessLevel));
        }

        return PetDetailDto.fromEntity(pet, user, Optional.empty());
    }



    @Transactional
    public PublicPetDto getPublicPetByQrToken(UUID token) {
        Pet pet = petRepo.findByQrCodeToken(token)
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

        /* ------------- PROPIETARIO ------------- */
        if ("PET_OWNER".equals(role)) {
            OwnerProfile owner = owners.findByUserId(user.getId()).orElseThrow();

            return petRepo.findByOwner(owner.getUser()).stream()
                    .map(pet -> {
                        // “Home clinic” = la primera clínica asociada; ajusta a tu modelo
                        Clinic link = clinics.findById(pet.getHomeClinicId()).orElse(null);
                        String clinicName = link != null ? link.getName() : null;
//                        PetClinic link = petClinics.findFirstByPetId(pet.getId()).orElse(null);
//                        String clinicName = link != null ? link.getClinic().getName() : null;
                        return PetListItemDto.forOwner(pet, clinicName);
                    })
                    .toList();
        }

        /* ------------- VETERINARIO ------------- */
        if ("VET".equals(role)) {
            VetProfile vet = vetRepo.findByUserId(user.getId()).orElseThrow();
            Long clinicId = vet.getClinic().getId();

            List<HistoricalRecordClinic> links = histClinics.findByClinicId(clinicId);
//            List<PetClinic> links = petClinics.findByClinicId(clinicId);

            return links.stream()
                    .map(link -> {

                        Pet pet = petRepo.findById(histories.findByPetId(link.getRecordId()).getFirst().getId()).orElse(null);
                        if (pet == null) return null;

                        String ownerName = pet.getOwner() != null
                                ? pet.getOwner().getUsername()
                                : null;

                        return PetListItemDto.forVet(
                                pet,
                                link.getAccessLevel().name(),   // READ_ONLY, WRITE, FULL…
                                ownerName
                        );
                    })
                    .filter(Objects::nonNull)
                    .toList();
        }

        throw new AccessDeniedException("Unauthorized role");
    }




//      Basic List
//    public List<PetListItemDto> listPets(User user) {
//        String role = user.getRole().getName();
//
//        if (role.equals("PET_OWNER")) {
//            OwnerProfile owner = owners.findByUserId(user.getId()).orElseThrow();
//            return pets.findByOwner(owner.getUser()).stream()
//                    .map(PetListItemDto::from)
//                    .toList();
//        }
//
//        if (role.equals("VET")) {
//            VetProfile vet = vets.findByUserId(user.getId()).orElseThrow();
//            Long clinicId = vet.getClinic().getId();
//
//            List<PetClinic> links = petClinics.findByClinicId(clinicId);
//            return links.stream()
//                    .map(link -> pets.findById(link.getPetId()).orElse(null))
//                    .filter(Objects::nonNull)
//                    .map(PetListItemDto::from)
//                    .toList();
//        }
//
//        throw new RuntimeException("Unauthorized role");
//    }
    // ------------


//
//    public List<HistoricalRecordDto> getFullHistoryForPet(Long petId) {
//        // Obtener todos los historiales de esa mascota (sin importar clínica)
//        List<PetHistoricalRecord> records = histories.findByPetId(petId);
//
//        return records.stream().map(record -> {
//            HistoricalRecordDto dto = new HistoricalRecordDto();
//            dto.setRecordId(record.getId());
//

    /// /            // Obtener la clínica (si querés mostrarla)
    /// /            HistoricalRecordClinic link = histClinics.findByRecordId(record.getId())
    /// /                    .orElse(null);
    /// /
    /// /            if (link != null) {
    /// /                dto.setClinic(ClinicDto.fromEntity(clinics.findById(link.getClinicId()).orElse(null)));
    /// /            }
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

//public List<HistoricalRecordDto> getFullHistoryForPet(Long petId) {
//    List<PetHistoricalRecord> records = histories.findByPetId(petId);
//
//    return records.stream().map(record -> {
//        HistoricalRecordDto dto = new HistoricalRecordDto();
//        dto.setRecordId(record.getId());
//
//        /* 1. TODAS las clínicas con acceso */
//        List<ClinicDto> clinicDtos = histClinics.findByRecordId(record.getId()).stream()
//                .map(link -> clinics.findById(link.getClinicId())
//                        .map(ClinicDto::fromEntity)
//                        .orElse(null))
//                .filter(Objects::nonNull)
//                .toList();
//        dto.setClinics(clinicDtos);
//
//        /* 2. Citas + nombre de clínica */
//        List<Appointment> appointments = appointmentsRepo.findByRecordId(record.getId());
//
//        List<AppointmentSummaryDto> summaries = appointments.stream().map(app -> {
//            AppointmentSummaryDto summary = new AppointmentSummaryDto();
//            summary.setId(app.getId());
//            summary.setDate(app.getDate());
//            summary.setWeight(app.getWeight());
//            summary.setTemperature(app.getTemperature());
//            summary.setHeartRate(app.getHeartRate());
//            summary.setDescription(app.getDescription());
//            summary.setTreatments(app.getTreatments());
//            summary.setDiagnosis(app.getDiagnosis());
//            summary.setNotes(app.getNotes());
//            summary.setCreatedById(app.getCreatedBy().getId());
//            summary.setSymptoms(symptomRepo.findNamesByAppointmentId(app.getId()));
//
//            /* 🔎 clínica responsable = clínica del vet creador */
//            Long clinicId = vets.findClinicIdByUserId(app.getCreatedBy().getId())
//                    .orElse(null);
//            summary.setClinicId(clinicId);
//            if (clinicId != null) {
//                clinics.findById(clinicId)
//                        .ifPresent(c -> summary.setClinicName(c.getName()));
//            }
//            return summary;
//        }).toList();
//
//        dto.setAppointments(summaries);
//        return dto;
//    }).toList();
//}
//


// --------------------------------------
// --------------------------------------
// --------------------------------------
    public Pet updatePet(UpdatePetRequest r, User user) {
        Pet pet = petRepo.findById(r.petId).orElseThrow(() -> new RuntimeException("Pet not found"));

        boolean isOwner = pet.getOwner() != null && pet.getOwner().getId().equals(user.getId());

        if (isOwner) {
            // FULL Owner: puede editar campos básicos
            applyBasicUpdates(pet, r);
            return petRepo.save(pet);
        }

        // VET
        VetProfile vet = vetRepo.findByUserId(user.getId()).orElseThrow();
        PetHistoricalRecord record = histories.findByPet(pet).orElseThrow();

        AccessLevelEnum access = histClinics.findByRecordIdAndClinicId(record.getId(), vet.getClinic().getId())
                .map(HistoricalRecordClinic::getAccessLevel)
                .orElseThrow(() -> new RuntimeException("No access level assigned"));
//    AccessLevelEnum access = histClinics.findAccessLevel(record.getId(), vet.getClinic().getId())
//            .orElseThrow(() -> new RuntimeException("No access to pet record"));

        boolean updated = false;

        if (access == AccessLevelEnum.FULL) {
            applyBasicUpdates(pet, r);
            updated = true;
        }

        if (access == AccessLevelEnum.FULL || access == AccessLevelEnum.WRITE) {
            if (r.ownerName != null) {
                pet.setOwnerName(r.ownerName);
                updated = true;
            }
            if (r.ownerContact != null) {
                pet.setOwnerContact(r.ownerContact);
                updated = true;
            }
        }

        if (!updated) {
            throw new RuntimeException("No editable fields allowed for your access level");
        }

        return petRepo.save(pet);
    }

    private void applyBasicUpdates(Pet pet, UpdatePetRequest r) {
        if (r.name != null) pet.setName(r.name);
        if (r.species != null) pet.setSpecies(r.species);
        if (r.breed != null) pet.setBreed(r.breed);
        if (r.sex != null) pet.setSex(SexEnum.valueOf(r.sex));
        if (r.status != null) pet.setStatus(PetStatusEnum.valueOf(r.status));
        if (r.birthdate != null) pet.setBirthdate(r.birthdate);
    }

//    private final PetRepository pets;
//    private final OwnerProfileRepository owners;
//    private final VetProfileRepository vets;
//    private final ClinicRepository clinics;
//    private final HistRecordClinicRepository histClinics;

    @Transactional
    public OwnerDetailDto getOwnerDetail(Long petId, User vetUser) {

        /* 1) validar rol */
        if (!"VET".equals(vetUser.getRole().getName())) {
            throw new AccessDeniedException("Solo veterinarios");
        }

        /* 2) clínica del vet */
        Long clinicId = vetRepo.findByUserId(vetUser.getId())
                .orElseThrow()
                .getClinic()
                .getId();

        /* 3) enlace clínica-registro (tienes dos opciones) -------------- */

        /* Opción A – JOIN con petId directamente */
//        HistoricalRecordClinic link = histClinics
//                .findByPetAndClinic(petId, clinicId)
//                .orElseThrow(() -> new AccessDeniedException("Sin enlace clínica-mascota"));

        /* Opción B – usar primero el registro y luego buscar por recordId */
         Long recordId = histories.findFirstByPet_Id(petId)
                                  .orElseThrow(() -> new AccessDeniedException("Mascota sin historial"))
                                  .getId();

         HistoricalRecordClinic link = histClinics
                 .findByRecordIdAndClinicId(recordId, clinicId)
                 .orElseThrow(() -> new AccessDeniedException("Sin enlace clínica-registro"));

        /* 4) validar nivel de acceso */
        if (!EnumSet.of(AccessLevelEnum.READ, AccessLevelEnum.WRITE, AccessLevelEnum.FULL)
                .contains(link.getAccessLevel())) {
            throw new AccessDeniedException("Nivel de acceso insuficiente");
        }

        /* 5) datos del dueño */
        Pet pet = petRepo.findById(petId).orElseThrow();
        OwnerProfile owner = owners.findByUserId(pet.getOwner().getId()).orElseThrow();

        return OwnerDetailDto.from(owner);
    }


    /* ---------- home clinic – visible para PET_OWNER ---------- */
    public ClinicDto getHomeClinicDetail(Long petId, User ownerUser) {

        if (!"PET_OWNER".equals(ownerUser.getRole().getName())) {
            throw new AccessDeniedException("Solo propietarios");
        }

        /* 1) comprobar que la mascota pertenece al usuario */
        OwnerProfile owner = owners.findByUserId(ownerUser.getId()).orElseThrow();
        Pet pet = petRepo.findByIdAndOwner(petId, owner.getUser())
                .orElseThrow(() -> new AccessDeniedException("Mascota no encontrada"));

        /* 2) home clinic */
        Clinic clinic = clinics.findById(pet.getHomeClinicId())
                .orElseThrow(() -> new AccessDeniedException("Sin clínica asociada"));

        return ClinicDto.fromEntity(clinic);
    }




    /// //////// EDIT CODE

    private String generateSecureEditCode() {
        return UUID.randomUUID().toString(); // reemplaza por hash/base62 si quieres
    }

    private Pet fetchPet(Long petId) {
        return petRepo.findById(petId)
                .orElseThrow(() -> new NotFoundException("Pet not found"));
    }

    private boolean userIsOwner(Pet p, User u) {
        return p.getOwner() != null && p.getOwner().getId().equals(u.getId());
    }

    private boolean clinicHasFull(Pet p, User vet) {
        // veterinario → buscamos su clínica
        return vet.getRole().getName().equals("VET") &&
                histClinics.hasFullAccess(p.getId(),
                        vetRepo.findById(vet.getId())
                                .orElseThrow().getClinic().getId());
    }

    private void assertCanAccess(Pet p, User u) {
        if (!(userIsOwner(p, u) || clinicHasFull(p, u))) {
            throw new AccessDeniedException("NOT_AUTH");
        }
    }

    /* ---------- endpoint services ---------- */
    public EditCodeResponse viewEditCode(Long petId, User user) {
        Pet pet = fetchPet(petId);
        assertCanAccess(pet, user);
        return new EditCodeResponse(pet.getEditCode());
    }

    public EditCodeResponse regenerateEditCode(Long petId, User user) {
        Pet pet = fetchPet(petId);
        assertCanAccess(pet, user);

        pet.setEditCode(generateSecureEditCode());
        petRepo.save(pet);
        return new EditCodeResponse(pet.getEditCode());
    }

}