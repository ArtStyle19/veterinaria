package com.vicgroup.veterinaria.modules.admin.dto.response;

import java.math.BigDecimal;

/**
 * Resumen de clínica usado en rankings globales (Top 5, etc.).
 */
public record TopClinicDto(
        Long clinicId,
        String clinicName,
        long totalAppointments,
        BigDecimal totalIncome
) {}