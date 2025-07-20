package com.vicgroup.veterinaria.modules.admin.dto.response;
import java.time.YearMonth;

/**
 * Conteo mensual de citas (u otra métrica).
 */
public record MonthlyCount(
        YearMonth month,
        long count
) {}