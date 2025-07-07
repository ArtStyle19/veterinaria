package com.vicgroup.veterinaria.repository;

import com.vicgroup.veterinaria.model.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;//
import java.util.Optional;

public interface SymptomRepo extends JpaRepository<Symptom, Long> {
    Optional<Symptom> findByName(String name);

    @Query("""
    SELECT s.name
    FROM Symptom s
    JOIN AppointmentSymptom aps ON aps.symptomId = s.id
    WHERE aps.appointmentId = :id
    """)
    List<String> findNamesByAppointmentId(@Param("id") Long id);

    @Query("SELECT s.name FROM Symptom s")
    List<String> findAllNames();
}
