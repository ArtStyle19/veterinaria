package com.vicgroup.veterinaria.modules.admin.controller;

import com.vicgroup.veterinaria.modules.admin.dto.response.GlobalStatsDto;
import com.vicgroup.veterinaria.modules.admin.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

@RestController
@RequestMapping("/admin/stats")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminStatsController {

    private final StatsService statsService;

    @GetMapping
    public GlobalStatsDto stats(
            @RequestParam @DateTimeFormat(iso = DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DATE) LocalDate to) {

        return statsService.getGlobalStats(from, to);
    }
}