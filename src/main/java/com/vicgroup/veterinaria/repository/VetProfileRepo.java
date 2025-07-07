package com.vicgroup.veterinaria.repository;

import com.vicgroup.veterinaria.model.VetProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VetProfileRepo extends JpaRepository<VetProfile, Long> {
    // You can add custom query methods here later if needed
    // Example: List<VetProfile> findByClinicId(Long clinicId);
    Optional<VetProfile> findByUserId(Long id);
}