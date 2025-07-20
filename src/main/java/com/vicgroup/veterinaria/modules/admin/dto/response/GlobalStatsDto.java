package com.vicgroup.veterinaria.modules.admin.dto.response;

import com.vicgroup.veterinaria.modules.admin.dto.response.DailyCount;
import com.vicgroup.veterinaria.modules.admin.dto.response.MonthlyCount;
import com.vicgroup.veterinaria.modules.admin.dto.response.SymptomCount;

import java.math.BigDecimal;
import java.util.List;

/**
 * Métricas agregadas de todo el sistema.
 */
public record GlobalStatsDto(
        List<DailyCount>  dailyAppointments,
        List<MonthlyCount> monthlyAppointments,
        long totalAppointments,

        BigDecimal totalIncome,      // histórico
        BigDecimal incomeThisMonth,

        long totalClinics,
        long totalLostDogs,

        List<SymptomCount> topSymptoms,
        List<TopClinicDto> topClinics   // ranking por ingresos o citas
) {}