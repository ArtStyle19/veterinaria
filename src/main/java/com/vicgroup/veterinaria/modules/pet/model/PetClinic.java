package com.vicgroup.veterinaria.modules.pet.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "pet_clinic")
@Getter @Setter @NoArgsConstructor
@IdClass(PetClinicId.class)
public class PetClinic {

    @Id
    private Long petId;

    @Id
    private Long clinicId;

    private Instant linkedAt = Instant.now();
}

