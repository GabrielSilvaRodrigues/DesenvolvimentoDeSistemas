package com.crud.backend.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EmailDTO {
    private String para;
    private String assunto;
    private String corpo;
    private String fromAddress="fatecmeets@gmail.com";
    private String fromName="Fatec Meets";
}

