package com.crud.backend.token;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
    Optional<TokenEntity> findByValor(String token);
    List<TokenEntity> findAllByUsuarioIdAndAtivoTrue(Long usuarioId);
    boolean existsByValorAndAtivoTrue(String token);
}
