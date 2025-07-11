package com.vicgroup.veterinaria.dto;

import com.vicgroup.veterinaria.model.enums.PetStatusEnum;
//import com.vicgroup.veterinaria.model.enums.SexEnum;

import java.time.LocalDate;

public class CreatePetRequest {
    public String name;
    public String species;
    public String breed;

    public String sex; // MALE | FEMALE | UNKNOWN
    public String status;


    public LocalDate birthdate;
    public Long homeClinicId;
}
