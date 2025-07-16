package com.vicgroup.veterinaria.dto;

import com.vicgroup.veterinaria.model.OwnerProfile;
import com.vicgroup.veterinaria.core.enums.SexEnum;
import com.vicgroup.veterinaria.model.Pet;
import com.vicgroup.veterinaria.model.Clinic;
import com.vicgroup.veterinaria.core.enums.PetStatusEnum;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class PublicPetDto {

    private UUID qrCodeToken;

    private String name;
    private String species;
    private String breed;
    private SexEnum sex;
    private LocalDate birthdate;
    private PetStatusEnum status;

    private ClinicInfo clinic;

    // Only included if status == LOST
    private String ownerContact;
    private String ownerEmail;

    private String ownerName;


    private boolean canBeImported;

    @Data
    public static class ClinicInfo {
        private String name;
        private String address;

        public static ClinicInfo from(Clinic c) {
            ClinicInfo info = new ClinicInfo();
            info.setName(c.getName());
            info.setAddress(c.getAddress());
            return info;
        }
    }

    public static PublicPetDto fromEntity(Pet p, Clinic c, String ownerContact, String ownerEmail) {
        PublicPetDto dto = new PublicPetDto();


        dto.setQrCodeToken(p.getQrCodeToken());


        dto.setName(p.getName());
        dto.setSpecies(p.getSpecies());
        dto.setBreed(p.getBreed());
        dto.setSex(p.getSex());
        dto.setBirthdate(p.getBirthdate());
        dto.setStatus(p.getStatus());
        dto.setClinic(ClinicInfo.from(c));

        dto.setCanBeImported(p.getOwner() == null); // solo si no tiene owner aún

        if (p.getStatus() == PetStatusEnum.LOST) {
            dto.setOwnerContact(ownerContact);
            dto.setOwnerEmail(ownerEmail);
        }

        return dto;
    }

    public static PublicPetDto fromEntity(Pet p, Clinic c, OwnerProfile owner) {
        PublicPetDto dto = new PublicPetDto();

        dto.setName(p.getName());
        dto.setSpecies(p.getSpecies());
        dto.setBreed(p.getBreed());
        dto.setSex(p.getSex());
        dto.setBirthdate(p.getBirthdate());
        dto.setStatus(p.getStatus());

        if (c != null) {
            dto.setClinic(PublicPetDto.ClinicInfo.from(c));
        }

        dto.setCanBeImported(p.getOwner() == null);

        boolean isLost = p.getStatus() == PetStatusEnum.LOST;

        if (isLost) {
            if (owner != null) {
                dto.setOwnerContact(owner.getCelNum());
                dto.setOwnerEmail(owner.getEmail());
                dto.setOwnerContact(p.getOwnerContact()); // vet-created fallback
                dto.setOwnerName(p.getOwnerName()); // ← ADD THIS!

//                dto.setOwnerName(owner.()); // or whatever method you use
            } else {
                dto.setOwnerContact(p.getOwnerContact()); // vet-created fallback
                dto.setOwnerEmail(null);
                dto.setOwnerName(p.getOwnerName()); // ← ADD THIS!
            }
        }

        return dto;
    }


}
