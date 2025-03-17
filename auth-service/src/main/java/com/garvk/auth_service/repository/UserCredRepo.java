package com.garvk.auth_service.repository;

import com.garvk.auth_service.model.UserCred;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredRepo extends JpaRepository<UserCred, Integer> {
    Optional<UserCred> findByName(String username);
}
