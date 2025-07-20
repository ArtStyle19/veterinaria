package com.vicgroup.veterinaria.modules.clinic.repository;

import com.vicgroup.veterinaria.modules.clinic.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClinicRepo extends JpaRepository<Clinic, Long> {
    // You can add custom query methods here later if needed
    // Example: List<Clinic> findByNameContaining(String name);
    @Query("SELECT COUNT(c) FROM Clinic c")
    long countAllClinics();

}