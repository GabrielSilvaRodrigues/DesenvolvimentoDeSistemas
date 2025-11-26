package com.crud.backend.notificacao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<NotificacaoEntity, Long> {
    List<NotificacaoEntity> findAllByUsuarioIdOrderByCriadaEmDesc(Long usuarioId);
    List<NotificacaoEntity> findAllByUsuarioIdAndLidaFalseOrderByCriadaEmDesc(Long usuarioId);
}
