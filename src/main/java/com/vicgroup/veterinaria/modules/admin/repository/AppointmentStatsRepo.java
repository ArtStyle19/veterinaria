package com.vicgroup.veterinaria.modules.admin.repository;


import com.vicgroup.veterinaria.modules.appointment.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * Consultas de alto rendimiento sobre la tabla <appointment>
 * y uniones con pet / clinic para el dashboard de administración.
 */
@Repository
public interface AppointmentStatsRepo extends JpaRepository<Appointment, Long> {

    /* ----------  CLÍNICA ESPECÍFICA  ---------- */

    /** Citas por día en un rango de fechas */
    @Query(value = """
        SELECT DATE(a.date)          AS day,
               COUNT(*)              AS cnt
        FROM   appointment a
        JOIN   pet_historical_record r ON r.id = a.record_id
        JOIN   pet p                  ON p.id = r.pet_id
        WHERE  p.home_clinic_id = :clinicId
          AND  a.date BETWEEN :startTs AND :endTs
        GROUP BY DATE(a.date)
        ORDER BY day
        """, nativeQuery = true)
    List<Object[]> findDailyCountsByClinic(
            @Param("clinicId") Long clinicId,
            @Param("startTs")  Timestamp startTs,
            @Param("endTs")    Timestamp endTs
    );

    /** Citas por mes desde la primera cita */
    @Query(value = """
        SELECT date_trunc('month', a.date)::date AS month,
               COUNT(*)                          AS cnt
        FROM   appointment a
        JOIN   pet_historical_record r ON r.id = a.record_id
        JOIN   pet p                  ON p.id = r.pet_id
        WHERE  p.home_clinic_id = :clinicId
        GROUP BY date_trunc('month', a.date)
        ORDER BY month
        """, nativeQuery = true)
    List<Object[]> findMonthlyCountsByClinic(@Param("clinicId") Long clinicId);

    /** Pacientes únicos atendidos en el rango */
    @Query(value = """
        SELECT COUNT(DISTINCT p.id)
        FROM   appointment a
        JOIN   pet_historical_record r ON r.id = a.record_id
        JOIN   pet p                  ON p.id = r.pet_id
        WHERE  p.home_clinic_id = :clinicId
          AND  a.date BETWEEN :startTs AND :endTs
        """, nativeQuery = true)
    long countUniquePatientsByClinic(
            @Param("clinicId") Long clinicId,
            @Param("startTs")  Timestamp startTs,
            @Param("endTs")    Timestamp endTs
    );

    /** Veterinarios activos (al menos una cita creada) */
    @Query(value = """
        SELECT COUNT(DISTINCT a.created_by)
        FROM   appointment a
        JOIN   pet_historical_record r ON r.id = a.record_id
        JOIN   pet p                  ON p.id = r.pet_id
        WHERE  p.home_clinic_id = :clinicId
          AND  a.date BETWEEN :startTs AND :endTs
        """, nativeQuery = true)
    long countActiveVetsByClinic(
            @Param("clinicId") Long clinicId,
            @Param("startTs")  Timestamp startTs,
            @Param("endTs")    Timestamp endTs
    );

    /** Top-N síntomas/enfermedades en la clínica */
    @Query(value = """
        SELECT s.name  AS symptom,
               COUNT(*) AS occ
        FROM   appointment_symptoms asp
        JOIN   symptoms s ON s.id = asp.symptom_id
        JOIN   appointment a ON a.id = asp.appointment_id
        JOIN   pet_historical_record r ON r.id = a.record_id
        JOIN   pet p ON p.id = r.pet_id
        WHERE  p.home_clinic_id = :clinicId
        GROUP BY s.name
        ORDER BY occ DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findTopSymptomsByClinic(
            @Param("clinicId") Long clinicId,
            @Param("limit")    int limit
    );

    /* ----------  GLOBALES (todas las clínicas)  ---------- */

    /** Citas por día – global */
    @Query(value = """
        SELECT DATE(a.date) AS day,
               COUNT(*)     AS cnt
        FROM   appointment a
        WHERE  a.date BETWEEN :startTs AND :endTs
        GROUP BY DATE(a.date)
        ORDER BY day
        """, nativeQuery = true)
    List<Object[]> findDailyCountsGlobal(
            @Param("startTs") Timestamp startTs,
            @Param("endTs")   Timestamp endTs
    );

    /** Citas por mes – global */
    @Query(value = """
        SELECT date_trunc('month', a.date)::date AS month,
               COUNT(*)                          AS cnt
        FROM   appointment a
        GROUP BY date_trunc('month', a.date)
        ORDER BY month
        """, nativeQuery = true)
    List<Object[]> findMonthlyCountsGlobal();

    /** Pacientes únicos – global */
    @Query(value = """
        SELECT COUNT(DISTINCT p.id)
        FROM   appointment a
        JOIN   pet_historical_record r ON r.id = a.record_id
        JOIN   pet p ON p.id = r.pet_id
        WHERE  a.date BETWEEN :startTs AND :endTs
        """, nativeQuery = true)
    long countUniquePatientsGlobal(
            @Param("startTs") Timestamp startTs,
            @Param("endTs")   Timestamp endTs
    );

    /** Veterinarios activos – global */
    @Query(value = """
        SELECT COUNT(DISTINCT a.created_by)
        FROM   appointment a
        WHERE  a.date BETWEEN :startTs AND :endTs
        """, nativeQuery = true)
    long countActiveVetsGlobal(
            @Param("startTs") Timestamp startTs,
            @Param("endTs")   Timestamp endTs
    );

    /** Top-N síntomas – global */
    @Query(value = """
        SELECT s.name, COUNT(*) AS occ
        FROM   appointment_symptoms asp
        JOIN   symptoms s ON s.id = asp.symptom_id
        JOIN   appointment a ON a.id = asp.appointment_id
        GROUP BY s.name
        ORDER BY occ DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findTopSymptomsGlobal(@Param("limit") int limit);

    /** Ranking de clínicas por número de citas (puedes multiplicar por 4 S/ en el Service) */
    @Query(value = """
        SELECT c.id        AS clinic_id,
               c.name      AS clinic_name,
               COUNT(*)    AS citas
        FROM   appointment a
        JOIN   pet_historical_record r ON r.id = a.record_id
        JOIN   pet p ON p.id = r.pet_id
        JOIN   clinic c ON c.id = p.home_clinic_id
        WHERE  a.date BETWEEN :startTs AND :endTs
        GROUP BY c.id, c.name
        ORDER BY citas DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findTopClinicsByAppointments(
            @Param("startTs") Timestamp startTs,
            @Param("endTs")   Timestamp endTs,
            @Param("limit")   int limit
    );
}
