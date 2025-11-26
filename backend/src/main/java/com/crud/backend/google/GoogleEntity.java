package com.crud.backend.google;

import com.crud.backend.usuario.UsuarioEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "google_users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class GoogleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String googleId;

    @Column(nullable = false)
    private String email;

    private String name;
    private String picture;
    private String locale;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;
}
