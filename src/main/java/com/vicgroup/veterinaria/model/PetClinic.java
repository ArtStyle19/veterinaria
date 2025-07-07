package com.vicgroup.veterinaria.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
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

