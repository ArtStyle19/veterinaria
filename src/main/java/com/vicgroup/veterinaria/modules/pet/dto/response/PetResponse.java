package com.vicgroup.veterinaria.modules.pet.dto.response;

import com.vicgroup.veterinaria.modules.pet.model.Pet;

import java.time.LocalDate;

public class PetResponse {
    public Long id;
    public String name;
    public String species;
    public String breed;
    public String sex;
    public LocalDate birthdate;
    public String ownerName;
    public String visibility;

    public PetResponse(Pet pet) {
        this.id = pet.getId();
        this.name = pet.getName();
        this.species = pet.getSpecies();
        this.breed = pet.getBreed();
        this.sex = pet.getSex() != null ? pet.getSex().name() : null;
        this.birthdate = pet.getBirthdate();
        this.ownerName = pet.getOwner() != null ? pet.getOwner().getUsername() : null;
        this.visibility = pet.getVisibility() != null ? pet.getVisibility().name() : null;
    }
}
