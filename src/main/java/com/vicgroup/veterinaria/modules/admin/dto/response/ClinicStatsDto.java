package com.vicgroup.veterinaria.modules.admin.dto.response;

import com.vicgroup.veterinaria.modules.admin.dto.response.DailyCount;
import com.vicgroup.veterinaria.modules.admin.dto.response.MonthlyCount;
import com.vicgroup.veterinaria.modules.admin.dto.response.SymptomCount;

import java.math.BigDecimal;
import java.util.List;

/**
 * Métricas completas para una clínica.
 */
public record ClinicStatsDto(
        List<DailyCount>  dailyAppointments,
        List<MonthlyCount> monthlyAppointments,
        long totalAppointments,

        BigDecimal totalIncome,      // totalAppointments * tarifa
        BigDecimal incomeThisMonth,

        long uniquePatients,
        long activeVets,
        long lostDogs,

        List<SymptomCount> topSymptoms
) {}