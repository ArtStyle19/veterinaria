package com.vicgroup.veterinaria.modules.admin.controller;

import com.vicgroup.veterinaria.modules.admin.dto.response.ClinicStatsDto;
import com.vicgroup.veterinaria.modules.admin.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

@RestController
@RequestMapping("/admin/clinics")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class ClinicStatsController {

    private final StatsService statsService;

    @GetMapping("/{clinicId}/stats")
    public ClinicStatsDto stats(
            @PathVariable Long clinicId,
            @RequestParam @DateTimeFormat(iso = DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DATE) LocalDate to) {

        return statsService.getClinicStats(clinicId, from, to);
    }
}