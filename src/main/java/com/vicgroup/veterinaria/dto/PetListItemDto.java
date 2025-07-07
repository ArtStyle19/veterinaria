// dto/PetListItemDto.java
package com.vicgroup.veterinaria.dto;

import com.vicgroup.veterinaria.model.Pet;
import lombok.Data;

@Data
public class PetListItemDto {
    private Long id;
    private String name;
    private String species;
    private String breed;
    private String status;

    public static PetListItemDto from(Pet p) {
        PetListItemDto dto = new PetListItemDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setSpecies(p.getSpecies());
        dto.setBreed(p.getBreed());
        dto.setStatus(p.getStatus().name());
        return dto;
    }
}
