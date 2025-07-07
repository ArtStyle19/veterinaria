package com.vicgroup.veterinaria.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class CreateAppointmentRequest {
    private Long recordId;
    private Instant date;
    @NotNull
    private BigDecimal weight;

    //    private Double weight;
//    private Double temperature;
    @NotNull
    private BigDecimal temperature;
    private short heartRate;

    private String description;
    private String treatments;
    private String diagnosis;
    private String notes;

    private List<String> symptoms;
}
