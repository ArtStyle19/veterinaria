package com.vicgroup.veterinaria.modules.admin.repository;

import com.vicgroup.veterinaria.modules.pet.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Consultas sobre el estado de las mascotas (p.e. perdidos).
 */
@Repository
public interface PetStatsRepo extends JpaRepository<Pet, Long> {

    /** Perros perdidos en TODAS las clínicas */
    @Query(value = """
    SELECT COUNT(*)
    FROM pet
    WHERE status = CAST(:status AS pet_status_enum)
    """, nativeQuery = true)
    long countLostDogsGlobal(@Param("status") String status);


    /** Perros perdidos por clínica */
    @Query(value = """
    SELECT COUNT(*)
    FROM pet
    WHERE status = CAST(:status AS pet_status_enum)
      AND home_clinic_id = :clinicId
    """, nativeQuery = true)
    long countLostDogsByClinic(@Param("status") String status, @Param("clinicId") Long clinicId);

}