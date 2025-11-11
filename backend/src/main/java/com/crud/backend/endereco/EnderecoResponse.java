package com.crud.backend.endereco;

import com.crud.backend.endereco.Endereco;
import com.crud.backend.viacep.ViaCepDTO;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class EnderecoResponse {
    private Endereco endereco;
    private ViaCepDTO viaCep;
}
