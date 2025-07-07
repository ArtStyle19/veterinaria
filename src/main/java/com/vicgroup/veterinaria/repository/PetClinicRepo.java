package com.vicgroup.veterinaria.repository;

import com.vicgroup.veterinaria.model.PetClinic;
import com.vicgroup.veterinaria.model.PetClinicId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetClinicRepo extends JpaRepository<PetClinic, PetClinicId> {
    boolean existsByPetIdAndClinicId(Long petId, Long clinicId);
    List<PetClinic> findByClinicId(Long clinicId);


}

