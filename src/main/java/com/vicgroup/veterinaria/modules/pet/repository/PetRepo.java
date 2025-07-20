package com.vicgroup.veterinaria.modules.pet.repository;

import java.util.List;
import java.util.UUID;

import com.vicgroup.veterinaria.modules.pet.model.Pet;
import com.vicgroup.veterinaria.modules.user.model.User;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PetRepo extends JpaRepository<Pet, Long> {
    Optional<Pet> findByQrCodeToken(UUID token);
    Optional<Pet> findByEditCode(String code);
    List<Pet> findByOwner(User owner);

    Optional<Pet> findByIdAndOwner_Id(Long id, Long ownerId);

        Optional<Pet> findByIdAndOwner(Long id, User owner);
//    Optional<Pet> findByIdAndOwner_Id(Long id, Long ownerId);
}
