package com.vicgroup.veterinaria.modules.appointment.model;

import jakarta.persistence.*;
import lombok.*;

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
