package com.vicgroup.veterinaria.modules.pet.controller;

import com.vicgroup.veterinaria.modules.clinic.dto.shared.ClinicDto;
import com.vicgroup.veterinaria.modules.pet.dto.request.CreatePetRequest;
import com.vicgroup.veterinaria.modules.pet.dto.request.CreatePetWithHistoryRequest;
import com.vicgroup.veterinaria.modules.pet.dto.request.ImportPetRequest;
import com.vicgroup.veterinaria.modules.pet.dto.request.UpdatePetRequest;
import com.vicgroup.veterinaria.modules.pet.dto.response.EditCodeResponse;
import com.vicgroup.veterinaria.modules.pet.dto.response.PetDetailDto;
import com.vicgroup.veterinaria.modules.pet.dto.response.PetResponse;
import com.vicgroup.veterinaria.modules.pet.dto.shared.PetListItemDto;
import com.vicgroup.veterinaria.modules.record.dto.response.HistoricalRecordDto;
import com.vicgroup.veterinaria.modules.user.dto.response.OwnerDetailDto;
import lombok.RequiredArgsConstructor; // For automatic constructor injection
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.; // Marks this class as a REST controller


import com.vicgroup.veterinaria.modules.pet.model.Pet;
import com.vicgroup.veterinaria.modules.user.model.User;
import com.vicgroup.veterinaria.modules.pet.service.PetService;

import java.util.List;


@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService service;
    private final PetService petService;

    @PostMapping
    @PreAuthorize("hasRole('PET_OWNER')")
    public ResponseEntity<PetResponse> createPet(@RequestBody CreatePetRequest r, @AuthenticationPrincipal User user) {
        Pet pet = service.createAsOwner(r, user);
        return ResponseEntity.ok(new PetResponse(pet));
    }

    @PostMapping("/with-history")
    @PreAuthorize("hasRole('VET')")
    public ResponseEntity<PetResponse> createPetWithHistory(@RequestBody CreatePetWithHistoryRequest r, @AuthenticationPrincipal User user) {
        Pet pet = service.createAsVet(r, user);
        return ResponseEntity.ok(new PetResponse(pet));
    }


    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('PET_OWNER', 'VET')")
    public ResponseEntity<String> importPet(@RequestBody ImportPetRequest r, @AuthenticationPrincipal User user) {
        service.importPet(r, user);
        return ResponseEntity.ok("Pet imported successfully");
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<PetDetailDto> getPetDetails(@PathVariable Long id) {
//        PetDetailDto dto = service.getPetDetails(id);
//        return ResponseEntity.ok(dto);
//    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VET', 'PET_OWNER')")
    public ResponseEntity<PetDetailDto> getPetDetails(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        PetDetailDto dto = service.getPetDetails(id, user);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<PetListItemDto>> getPets(@AuthenticationPrincipal User user) {
        List<PetListItemDto> list = service.listPets(user);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('VET', 'PET_OWNER')")
    public ResponseEntity<PetResponse> updatePet(
            @PathVariable Long id,
            @RequestBody UpdatePetRequest request,
            @AuthenticationPrincipal User user
    ) {
        request.petId = id; // aseguramos que venga del path
        Pet updated = service.updatePet(request, user);
        return ResponseEntity.ok(new PetResponse(updated));
    }

    /* --- detalle del dueño (solo veterinarios) ---------------------------- */
    @GetMapping("/{petId}/owner")
    public ResponseEntity<OwnerDetailDto> ownerDetail(
            @PathVariable Long petId,
            @AuthenticationPrincipal User vetUser) {
        OwnerDetailDto dto = service.getOwnerDetail(petId, vetUser);
        return ResponseEntity.ok(dto);
    }

    /* --- detalle de la home-clinic (solo propietarios) --------------------- */
    @GetMapping("/{petId}/home-clinic")
    public ResponseEntity<ClinicDto> homeClinicDetail(
            @PathVariable Long petId,
            @AuthenticationPrincipal User ownerUser) {
        ClinicDto dto = service.getHomeClinicDetail(petId, ownerUser);
        return ResponseEntity.ok(dto);
    }



    /* 1️⃣  ver código */
    @GetMapping("/{id}/edit-code")
    @PreAuthorize("hasAnyRole('PET_OWNER','VET')")
    public ResponseEntity<EditCodeResponse> getEditCode(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(service.viewEditCode(id, user));
    }

    /* 2️⃣  regenerar código */
    @PostMapping("/{id}/edit-code/regenerate")
    @PreAuthorize("hasAnyRole('PET_OWNER','VET')")
    public ResponseEntity<EditCodeResponse> regenerateEditCode(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(service.regenerateEditCode(id, user));
    }

//    @GetMapping("/{id}/history")
//    @PreAuthorize("hasAnyRole('VET', 'PET_OWNER')") // o el rol que necesites
//    public ResponseEntity<List<HistoricalRecordDto>> getFullHistory(
//            @PathVariable Long id
//    ) {
//        List<HistoricalRecordDto> history = petService.getFullHistoryForPet(id);
//        return ResponseEntity.ok(history);
//    }


//    @PostMapping("/records/{recordId}/appointments")
//    @PreAuthorize("hasRole('VET')")
//    public ResponseEntity<?> createAppointment(
//            @PathVariable Long recordId,
//            @RequestBody @Valid CreateAppointmentRequest req,
//            @AuthenticationPrincipal User vetUser
//    ) {
//        service.createAppointment(recordId, req, vetUser);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }


//    @PostMapping("/import")
//    @PreAuthorize("hasRole('PET_OWNER')")
//    public ResponseEntity<String> importPet(@RequestBody ImportPetRequest r, @AuthenticationPrincipal User user) {
//        service.importPet(r, user);
//        return ResponseEntity.ok("Pet linked successfully");
//    }
}

//
//@RestController
//@RequestMapping("/api/pets")
//@RequiredArgsConstructor
//public class PetController {
//
//    private final PetService service;
//
//    @PostMapping
//    @PreAuthorize("hasRole('PET_OWNER')")
//    public ResponseEntity<Pet> createPet(@RequestBody CreatePetRequest r, @AuthenticationPrincipal User user) {
//        return ResponseEntity.ok(service.createAsOwner(r, user));
//    }
//
//    @PostMapping("/with-history")
//    @PreAuthorize("hasRole('VET')")
//    public ResponseEntity<Pet> createPetWithHistory(@RequestBody CreatePetWithHistoryRequest r, @AuthenticationPrincipal User user) {
//        return ResponseEntity.ok(service.createAsVet(r, user));
//    }
//
//    @PostMapping("/import")
//    @PreAuthorize("hasRole('PET_OWNER')")
//    public ResponseEntity<String> importPet(@RequestBody ImportPetRequest r, @AuthenticationPrincipal User user) {
//        service.importPet(r, user);
//        return ResponseEntity.ok("Pet linked successfully");
//    }
//}
