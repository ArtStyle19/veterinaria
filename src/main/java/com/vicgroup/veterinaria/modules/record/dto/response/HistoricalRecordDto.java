package com.vicgroup.veterinaria.modules.record.dto.response;

import com.vicgroup.veterinaria.modules.appointment.dto.shared.AppointmentSummaryDto;
import com.vicgroup.veterinaria.modules.clinic.dto.shared.ClinicDto;
import lombok.Data;

import java.util.List;

@Data
public class HistoricalRecordDto {
    private Long recordId;
//    private ClinicDto clinic; // o ClinicInfo
    private List<ClinicDto> clinics;                     // ⬅ NUEVO
    private List<AppointmentSummaryDto> appointments;
}
