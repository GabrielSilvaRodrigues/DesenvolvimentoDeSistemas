package com.crud.backend.endereco;

import com.crud.backend.common.Auditable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Table(name = "enderecos")
public class EnderecoEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 8)
    private String cep;

    @Column(length = 255)
    private String complemento;

    @Column(nullable = false)
    private Integer numero;
}
