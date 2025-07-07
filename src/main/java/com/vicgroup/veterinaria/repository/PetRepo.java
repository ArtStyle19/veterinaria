package com.vicgroup.veterinaria.repository;

import java.util.List;
import java.util.UUID;

import com.vicgroup.veterinaria.model.Pet;
import com.vicgroup.veterinaria.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PetRepo extends JpaRepository<Pet, Long> {
    Optional<Pet> findByQrCodeToken(UUID token);
    Optional<Pet> findByEditCode(String code);
    List<Pet> findByOwner(User owner);
}
