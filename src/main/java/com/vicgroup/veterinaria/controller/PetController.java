package com.vicgroup.veterinaria.controller;

import com.vicgroup.veterinaria.dto.*;
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

    @GetMapping("/{id}")
    public ResponseEntity<PetDetailDto> getPetDetails(@PathVariable Long id) {
        PetDetailDto dto = service.getPetDetails(id);
        return ResponseEntity.ok(dto);
    }


    @GetMapping
    public ResponseEntity<List<PetListItemDto>> getPets(@AuthenticationPrincipal User user) {
        List<PetListItemDto> list = service.listPets(user);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasRole('VET')") // o el rol que necesites
    public ResponseEntity<List<HistoricalRecordDto>> getFullHistory(
            @PathVariable Long id
    ) {
        List<HistoricalRecordDto> history = petService.getFullHistoryForPet(id);
        return ResponseEntity.ok(history);
    }


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
