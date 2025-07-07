package com.vicgroup.veterinaria.dto;

import java.time.LocalDate;

public class CreatePetRequest {
    public String name;
    public String species;
    public String breed;

    public String sex; // MALE | FEMALE | UNKNOWN
    public LocalDate birthdate;
    public Long homeClinicId;
}
