package com.crud.backend.token;

import java.time.LocalDateTime;

import com.crud.backend.common.Auditable;
import com.crud.backend.usuario.UsuarioEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "token", indexes = {
    @Index(name = "idx_token_valor", columnList = "valor", unique = true),
    @Index(name = "idx_token_usuario", columnList = "usuario_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 512)
    private String valor;

    @Column(length = 100)
    private String dispositivo;

    @Column(length = 45)
    private String ip;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TokenEnum tipo;

    @Column(nullable = false)
    @Builder.Default
    private boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @Column(name = "expira_em")
    private LocalDateTime expiraEm;
}
