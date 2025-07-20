package com.vicgroup.veterinaria.modules.pet.repository;

import com.vicgroup.veterinaria.modules.pet.model.PetClinic;
import com.vicgroup.veterinaria.modules.pet.model.PetClinicId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetClinicRepo extends JpaRepository<PetClinic, PetClinicId> {
    boolean existsByPetIdAndClinicId(Long petId, Long clinicId);
    List<PetClinic> findByClinicId(Long clinicId);


}

