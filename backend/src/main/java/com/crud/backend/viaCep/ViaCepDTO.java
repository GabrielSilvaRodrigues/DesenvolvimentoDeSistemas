package com.crud.backend.viaCep;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ViaCepDTO {
    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String localidade;
    private String uf;
}
