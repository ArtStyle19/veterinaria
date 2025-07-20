// dto/PetListItemDto.java
package com.vicgroup.veterinaria.modules.pet.dto.shared;

import com.vicgroup.veterinaria.modules.pet.model.Pet;
import lombok.Data;

//@Data
//public class PetListItemDto {
//    private Long id;
//    private String name;
//    private String species;
//    private String breed;
//    private String status;
//
//    public static PetListItemDto from(Pet p) {
//        PetListItemDto dto = new PetListItemDto();
//        dto.setId(p.getId());
//        dto.setName(p.getName());
//        dto.setSpecies(p.getSpecies());
//        dto.setBreed(p.getBreed());
//        dto.setStatus(p.getStatus().name());
//        return dto;
//    }
//}


// dto/PetListItemDto.java

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vicgroup.veterinaria.modules.pet.model.Pet;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)       // ← oculta los campos que queden null
public class PetListItemDto {

    /* Siempre */
    private Long id;
    private String name;
    private String species;
    private String breed;
    private String status;

    /* Extras opcionales ---------------------------- */
    private LocalDate birthdate;                 // para todos, si existe
    private String ownerName;                    // sólo lo ve un veterinario
    private String accessLevel;                  // sólo lo ve un veterinario
    private String homeClinic;                   // sólo lo ve el propietario

    /* ---------- FACTORÍAS ---------- */

    /** Mapea para un propietario. */
    public static PetListItemDto forOwner(Pet p, String homeClinicName) {
        PetListItemDto dto = base(p);
        dto.setHomeClinic(homeClinicName);
        return dto;
    }

    /** Mapea para un veterinario. */
    public static PetListItemDto forVet(Pet p, String accessLevel, String ownerFullName) {
        PetListItemDto dto = base(p);
        dto.setAccessLevel(accessLevel);
        dto.setOwnerName(ownerFullName);
        return dto;
    }

    /** Datos comunes. */
    private static PetListItemDto base(Pet p) {
        PetListItemDto dto = new PetListItemDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setSpecies(p.getSpecies());
        dto.setBreed(p.getBreed());
        dto.setStatus(p.getStatus().name());
        dto.setBirthdate(p.getBirthdate());      // si no existe quedará null y no se serializa
        return dto;
    }
}
