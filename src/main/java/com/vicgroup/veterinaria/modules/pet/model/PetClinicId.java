package com.vicgroup.veterinaria.modules.pet.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter @NoArgsConstructor @EqualsAndHashCode
public class PetClinicId implements Serializable {
    private Long petId;
    private Long clinicId;
}
