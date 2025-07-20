package com.vicgroup.veterinaria.modules.admin.dto.response;


import java.time.LocalDate;

/**
 * Conteo diario de citas (u otra métrica).
 */
public record DailyCount(
        LocalDate day,
        long count
) {}
