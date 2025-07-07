package com.vicgroup.veterinaria.dto;

import lombok.Data;

import java.util.List;

@Data
public class HistoricalRecordDto {
    private Long recordId;
    private ClinicDto clinic; // o ClinicInfo
    private List<AppointmentSummaryDto> appointments;
}
