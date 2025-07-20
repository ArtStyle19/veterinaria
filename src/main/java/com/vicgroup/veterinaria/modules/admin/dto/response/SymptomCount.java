package com.vicgroup.veterinaria.modules.admin.dto.response;

/**
 * Ocurrencias de un síntoma/enfermedad.
 */
public record SymptomCount(
        String symptom,
        long occurrences
) {}