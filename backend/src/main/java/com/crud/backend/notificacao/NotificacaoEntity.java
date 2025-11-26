package com.crud.backend.notificacao;

import com.crud.backend.common.Auditable;
import com.crud.backend.usuario.UsuarioEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacoes", indexes = {
        @Index(name = "idx_notificacao_usuario", columnList = "usuario_id"),
        @Index(name = "idx_notificacao_lida", columnList = "lida")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificacaoEntity extends Auditable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificacaoType tipo;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Column(nullable = false)
    @Builder.Default
    private boolean lida = false;

    @Column(name = "criada_em", nullable = false)
    private LocalDateTime criadaEm;

    // factory helper
    public static NotificacaoEntity of(UsuarioEntity usuario, NotificacaoType tipo, String titulo, String mensagem) {
        return NotificacaoEntity.builder()
                .usuario(usuario)
                .tipo(tipo)
                .titulo(titulo)
                .mensagem(mensagem)
                .lida(false)
                .criadaEm(LocalDateTime.now())
                .build();
    }
}
