package com.crud.backend.viaCep;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.StringUtils;

@Service
public class ViaCepService {

    private static final String URL = "https://viacep.com.br/ws/%s/json/";

    private final RestTemplate restTemplate;

    public ViaCepService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ViaCepDTO buscarCep(String cep) {
        if (!StringUtils.hasText(cep)) return null;
        String normalized = cep.replaceAll("\\D", "");
        if (normalized.length() != 8) return null;
        return restTemplate.getForObject(URL.formatted(normalized), ViaCepDTO.class);
    }
}
