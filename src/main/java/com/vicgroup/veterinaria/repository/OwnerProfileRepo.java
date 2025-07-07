package com.vicgroup.veterinaria.repository;

import com.vicgroup.veterinaria.model.OwnerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OwnerProfileRepo extends JpaRepository<OwnerProfile, Long> {
    // Custom methods if needed
    Optional<OwnerProfile> findByUserId(Long id);

}