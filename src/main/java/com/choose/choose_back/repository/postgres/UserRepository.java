package com.choose.choose_back.repository.postgres;

import com.choose.choose_back.domain.postgres.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}
