package com.vicgroup.veterinaria.modules.appointment.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class AppointmentDetailDto {
    private Long id;
    private Instant date;
    private BigDecimal weight;
    private BigDecimal temperature;
    private Short heartRate;
    private String description;
    private String treatments;
    private String diagnosis;
    private String notes;
    private Long createdById;
    private List<String> symptoms;

    private Long clinicId;
    private String clinicName;
}
