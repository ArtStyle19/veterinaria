package com.vicgroup.veterinaria.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "appointment_symptoms")
@Getter @Setter @NoArgsConstructor
@IdClass(AppointmentSymptomId.class)
public class AppointmentSymptom {

    @Id
    @Column(name = "appointment_id")
    private Long appointmentId;

    @Id
    @Column(name = "symptom_id")
    private Long symptomId;
}
