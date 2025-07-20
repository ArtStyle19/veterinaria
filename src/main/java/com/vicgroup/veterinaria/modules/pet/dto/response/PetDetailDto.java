package com.vicgroup.veterinaria.modules.pet.dto.response;

import com.vicgroup.veterinaria.core.enums.AccessLevelEnum;
import com.vicgroup.veterinaria.core.enums.SexEnum;
import com.vicgroup.veterinaria.core.enums.VisibilityEnum;
import com.vicgroup.veterinaria.modules.pet.model.Pet;
import com.vicgroup.veterinaria.modules.user.model.User;
import lombok.Data;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Data
public class PetDetailDto {


    private UUID qrCodeToken;              // ← NUEVO
    // DEPENDIENDO DE LA CUENTA LOEGADA VER QUE TIPO DE ACCESO TIENE || Decide in which response
    private AccessLevelEnum accessLevelEnum;
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

    public static PetDetailDto fromEntity(Pet pet, User user, Optional <AccessLevelEnum> accessOpt) {
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


        if (pet.getOwner() != null && pet.getOwner().getId().equals(user.getId())) {
            dto.setAccessLevelEnum(AccessLevelEnum.FULL_OWNER);
        } else if (user.getRole().getName().equals("VET") && accessOpt.isPresent()) {
            switch (accessOpt.get()) {
                case FULL -> dto.setAccessLevelEnum(AccessLevelEnum.FULL);
                case WRITE -> dto.setAccessLevelEnum(AccessLevelEnum.WRITE);
                default -> dto.setAccessLevelEnum(AccessLevelEnum.NONE);
            }
        } else {
            dto.setAccessLevelEnum(AccessLevelEnum.NONE);
        }
        return dto;
    }
}
