package com.crud.backend.usuario;

import com.crud.backend.common.Auditable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="usuarios", indexes = {
    @Index(name="uk_usuario_email", columnList = "email", unique = true), 
    @Index(name="uk_usuario_status", columnList = "status", unique = false)
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UsuarioEntity extends Auditable {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=true, length=255, unique=false)
    private String nome;

    @Column(nullable=true, length=255, unique=true)
    private String email;

    @Column(nullable=true, length=255, unique=false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private UsuarioEnum status = UsuarioEnum.PENDENTE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private OAuth2Enum oauth2 = OAuth2Enum.FALSE;

    @Column(name = "profile_image", length = 1024, nullable = true)
    private String profileImage;
}
