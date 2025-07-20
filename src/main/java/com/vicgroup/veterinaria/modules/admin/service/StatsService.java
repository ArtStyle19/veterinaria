package com.vicgroup.veterinaria.modules.admin.service;


import com.vicgroup.veterinaria.modules.admin.dto.response.*;
import com.vicgroup.veterinaria.modules.admin.repository.AppointmentStatsRepo;
import com.vicgroup.veterinaria.modules.admin.repository.PetStatsRepo;
import com.vicgroup.veterinaria.modules.clinic.repository.ClinicRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.*;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Servicio de métricas para el dashboard de administración.
 * <p>
 *  – Tarifa por cita: S/ 4.00 <br>
 *  – Rango de fechas: cualquier periodo; si es null se toma
 *    desde la primera cita hasta hoy.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final AppointmentStatsRepo appointmentRepo;
    private final PetStatsRepo         petRepo;
    private final ClinicRepo           clinicRepo;

    /** Tarifa fija por cita (moneda PEN – soles peruanos). */
    private static final BigDecimal APPOINTMENT_FEE = new BigDecimal("4.00");
    /** Número de ítems a mostrar en gráficos de ranking/top-N. */
    private static final int TOP_N = 5;

    /* -------------------------------------------------------------------- */
    /*                               CLÍNICA                                */
    /* -------------------------------------------------------------------- */

    public ClinicStatsDto getClinicStats(Long clinicId,
                                         LocalDate from,
                                         LocalDate to) {

        Objects.requireNonNull(clinicId, "clinicId no puede ser null");

        // Si el usuario no manda fechas => desde primera cita hasta hoy
        if (from == null) {
            from = LocalDate.of(2020, 1, 1);   // o tu fecha mínima lógica
        }
        if (to == null) {
            to = LocalDate.now();
        }

        Timestamp startTs = Timestamp.valueOf(from.atStartOfDay());
        Timestamp endTs   = Timestamp.valueOf(to.plusDays(1).atStartOfDay()); // inclusivo

        /* -----------  Métricas principales  ----------- */

        // Series diarias
        List<DailyCount> daily = appointmentRepo.findDailyCountsByClinic(clinicId, startTs, endTs)
                .stream()
                .map(arr -> new DailyCount(
                        ((java.sql.Date) arr[0]).toLocalDate(),
                        ((Number)       arr[1]).longValue()))
                .toList();

        // Series mensuales (sin filtro de fechas: histórico completo)
        List<MonthlyCount> monthly = appointmentRepo.findMonthlyCountsByClinic(clinicId)
                .stream()
                .map(arr -> new MonthlyCount(
                        YearMonth.from(((java.sql.Date) arr[0]).toLocalDate()),
                        ((Number) arr[1]).longValue()))
                .toList();

        long totalAppointments = monthly.stream()
                .mapToLong(MonthlyCount::count)
                .sum();

        BigDecimal totalIncome = APPOINTMENT_FEE
                .multiply(BigDecimal.valueOf(totalAppointments));

        YearMonth currentMonth = YearMonth.now();
        long thisMonthAppointments = monthly.stream()
                .filter(m -> m.month().equals(currentMonth))
                .mapToLong(MonthlyCount::count)
                .findFirst()
                .orElse(0);

        BigDecimal incomeThisMonth = APPOINTMENT_FEE
                .multiply(BigDecimal.valueOf(thisMonthAppointments));

        // Pacientes y vets en el rango solicitado (from-to)
        long uniquePatients = appointmentRepo.countUniquePatientsByClinic(clinicId, startTs, endTs);
        long activeVets     = appointmentRepo.countActiveVetsByClinic   (clinicId, startTs, endTs);

        // Perros perdidos de esa clínica
        long lostDogs = petRepo.countLostDogsByClinic("LOST", clinicId);

        // Enfermedades / síntomas más comunes
        List<SymptomCount> topSymptoms = appointmentRepo.findTopSymptomsByClinic(clinicId, TOP_N)
                .stream()
                .map(arr -> new SymptomCount(
                        (String)  arr[0],
                        ((Number) arr[1]).longValue()))
                .toList();

        return new ClinicStatsDto(
                daily,
                monthly,
                totalAppointments,
                totalIncome,
                incomeThisMonth,
                uniquePatients,
                activeVets,
                lostDogs,
                topSymptoms
        );
    }

    /* -------------------------------------------------------------------- */
    /*                                GLOBAL                                */
    /* -------------------------------------------------------------------- */

    public GlobalStatsDto getGlobalStats(LocalDate from, LocalDate to) {

        if (from == null) { from = LocalDate.of(2020, 1, 1); }
        if (to   == null) { to   = LocalDate.now(); }

        Timestamp startTs = Timestamp.valueOf(from.atStartOfDay());
        Timestamp endTs   = Timestamp.valueOf(to.plusDays(1).atStartOfDay());

        /* -----------  Series diarias y mensuales  ----------- */

        List<DailyCount> daily = appointmentRepo.findDailyCountsGlobal(startTs, endTs)
                .stream()
                .map(arr -> new DailyCount(
                        ((java.sql.Date) arr[0]).toLocalDate(),
                        ((Number)       arr[1]).longValue()))
                .toList();

        List<MonthlyCount> monthly = appointmentRepo.findMonthlyCountsGlobal()
                .stream()
                .map(arr -> new MonthlyCount(
                        YearMonth.from(((java.sql.Date) arr[0]).toLocalDate()),
                        ((Number) arr[1]).longValue()))
                .toList();

        long totalAppointments = monthly.stream()
                .mapToLong(MonthlyCount::count)
                .sum();

        BigDecimal totalIncome = APPOINTMENT_FEE
                .multiply(BigDecimal.valueOf(totalAppointments));

        YearMonth currentMonth = YearMonth.now();
        long thisMonthAppointments = monthly.stream()
                .filter(m -> m.month().equals(currentMonth))
                .mapToLong(MonthlyCount::count)
                .findFirst()
                .orElse(0);

        BigDecimal incomeThisMonth = APPOINTMENT_FEE
                .multiply(BigDecimal.valueOf(thisMonthAppointments));

        /* -----------  Datos agregados  ----------- */

        long totalClinics  = clinicRepo.countAllClinics();
        long totalLostDogs = petRepo.countLostDogsGlobal("LOST");

        List<SymptomCount> topSymptoms = appointmentRepo.findTopSymptomsGlobal(TOP_N)
                .stream()
                .map(arr -> new SymptomCount(
                        (String)  arr[0],
                        ((Number) arr[1]).longValue()))
                .toList();

        /* -----------  Ranking de clínicas  ----------- */

        List<TopClinicDto> topClinics = appointmentRepo.findTopClinicsByAppointments(
                        startTs, endTs, TOP_N)
                .stream()
                .map(arr -> {
                    long   clinicId    = ((Number) arr[0]).longValue();
                    String clinicName  = (String)  arr[1];
                    long   citas       = ((Number) arr[2]).longValue();
                    BigDecimal income  = APPOINTMENT_FEE.multiply(BigDecimal.valueOf(citas));
                    return new TopClinicDto(clinicId, clinicName, citas, income);
                })
                .sorted(Comparator.comparing(TopClinicDto::totalIncome).reversed())
                .collect(Collectors.toList());

        return new GlobalStatsDto(
                daily,
                monthly,
                totalAppointments,
                totalIncome,
                incomeThisMonth,
                totalClinics,
                totalLostDogs,
                topSymptoms,
                topClinics
        );
    }
}
