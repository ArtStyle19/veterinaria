package com.vicgroup.veterinaria.modules.pet.controller;

import com.vicgroup.veterinaria.modules.pet.dto._public.PublicPetDto;
import com.vicgroup.veterinaria.modules.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/public/pets")
@RequiredArgsConstructor
public class PublicPetController {

    private final PetService service;

    @GetMapping("/qr/{token}")
    public ResponseEntity<PublicPetDto> getPublicPet(@PathVariable UUID token) {
        PublicPetDto dto = service.getPublicPetByQrToken(token);
        return ResponseEntity.ok(dto);
    }
}
