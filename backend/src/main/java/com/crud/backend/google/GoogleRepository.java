package com.crud.backend.google;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GoogleRepository extends JpaRepository<GoogleEntity, Long> {

    Optional<GoogleEntity> findByGoogleId(String googleId);

    Optional<GoogleEntity> findByEmail(String email);
}
