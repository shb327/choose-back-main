package com.choose.choose_back.repository.postgres;

import com.choose.choose_back.domain.postgres.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("FROM User WHERE username = :username OR email = :username")
    Optional<User> findByUsernameOrEmail(String username);

    Optional<User> findByUsername(String username);
}
