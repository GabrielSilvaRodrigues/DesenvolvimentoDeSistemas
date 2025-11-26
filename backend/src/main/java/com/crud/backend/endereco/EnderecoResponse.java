package com.crud.backend.endereco;

import com.crud.backend.viaCep.ViaCepDTO;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class EnderecoResponse {
    private EnderecoEntity endereco;
    private ViaCepDTO viaCep;
}
