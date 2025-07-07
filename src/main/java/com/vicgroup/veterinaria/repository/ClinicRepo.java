package com.vicgroup.veterinaria.repository;

import com.vicgroup.veterinaria.model.Clinic;
import com.vicgroup.veterinaria.model.PetClinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClinicRepo extends JpaRepository<Clinic, Long> {
    // You can add custom query methods here later if needed
    // Example: List<Clinic> findByNameContaining(String name);
}