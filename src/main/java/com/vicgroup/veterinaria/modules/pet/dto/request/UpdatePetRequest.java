package com.vicgroup.veterinaria.modules.pet.dto.request;

import java.time.LocalDate;

public class UpdatePetRequest {
    public Long petId;

    // Campos básicos (solo editables con acceso FULL o por el dueño)
    public String name;
    public String species;
    public String breed;
    public String sex;         // MALE | FEMALE | UNKNOWN
    public String status;      // LOST, OK, SICK, DECEASED
    public LocalDate birthdate;

    // Editables con FULL y WRITE (vets), o FULL Owner
    // Cambiar esto una tabla mas ya que esto deberia ser solo para la vista de 1 clinica en singular
    public String ownerName;
    public String ownerContact;
}
