package com.vicgroup.veterinaria.modules.user.repository;

import com.vicgroup.veterinaria.modules.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.id = :id")
    Optional<User> findByIdWithRole(@Param("id") Long id);
}

//@Repository
//public interface UserRepo extends JpaRepository<User, Long> {
//    Optional<User> findByUsername(String username);
//}