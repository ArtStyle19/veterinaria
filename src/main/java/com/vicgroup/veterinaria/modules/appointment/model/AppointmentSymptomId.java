package com.vicgroup.veterinaria.modules.appointment.model;

import lombok.*;

import java.io.Serializable;

@Getter @Setter @NoArgsConstructor @EqualsAndHashCode
public class AppointmentSymptomId implements Serializable {
    private Long appointmentId;
    private Long symptomId;
}
