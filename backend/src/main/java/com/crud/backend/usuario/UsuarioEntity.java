package com.crud.backend.usuario;

import com.crud.backend.common.Auditable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="usuario", indexes = {
    @Index(name="uk_usuario_email", columnList = "email", unique = true), 
    @Index(name="uk_usuario_status", columnList = "status", unique = false)
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario extends Auditable {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=255, unique=false)
    private String nome;

    @Column(nullable=false, length=255, unique=true)
    private String email;

    @Column(nullable=false, length=255, unique=false)
    private String senha;

    @Enumerated(EnumType.STRING)
    private UsuarioEnum status = UsuarioEnum.PEDENTE;
}
