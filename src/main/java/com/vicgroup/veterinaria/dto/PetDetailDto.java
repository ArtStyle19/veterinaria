package com.vicgroup.veterinaria.dto;

import com.vicgroup.veterinaria.core.enums.SexEnum;
import com.vicgroup.veterinaria.core.enums.VisibilityEnum;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class PetDetailDto {


    private UUID qrCodeToken;              // ← NUEVO

    private Long id;
    private String name;
    private String species;
    private String breed;
    private SexEnum sex;
    private LocalDate birthdate;
    private String ownerName;
    private String ownerContact;
    private Long homeClinicId;
    private VisibilityEnum visibility;

    public static PetDetailDto fromEntity(com.vicgroup.veterinaria.model.Pet pet) {
        PetDetailDto dto = new PetDetailDto();


        dto.setQrCodeToken(pet.getQrCodeToken());


        dto.setId(pet.getId());
        dto.setName(pet.getName());
        dto.setSpecies(pet.getSpecies());
        dto.setBreed(pet.getBreed());
        dto.setSex(pet.getSex());
        dto.setBirthdate(pet.getBirthdate());
        dto.setOwnerName(pet.getOwnerName());
        dto.setOwnerContact(pet.getOwnerContact());
        dto.setHomeClinicId(pet.getHomeClinicId());
        dto.setVisibility(pet.getVisibility());
        return dto;
    }
}
