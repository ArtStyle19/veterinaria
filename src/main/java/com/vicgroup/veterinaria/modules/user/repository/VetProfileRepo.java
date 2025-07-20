package com.vicgroup.veterinaria.modules.user.repository;

import com.vicgroup.veterinaria.modules.user.model.VetProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository

public interface VetProfileRepo extends JpaRepository<VetProfile, Long> {
    // You can add custom query methods here later if needed
    // Example: List<VetProfile> findByClinicId(Long clinicId);
    Optional<VetProfile> findByUserId(Long id);
    /* VetProfileRepo ya existe, añadimos helper */
    @Autowired
    @Query("""
  SELECT vp.clinic.id FROM VetProfile vp
  WHERE vp.user.id = :userId
""")
    Optional<Long> findClinicIdByUserId(@Param("userId") Long userId);

}