package com.crud.backend.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
public class GithubDriveService {

    // token é lido do application.properties
    @Value("${github.token}")
    private String githubToken;

    // repositório / branch (valor padrão conforme pedido)
    private final String repoOwner = "GabrielSilvaRodrigues";
    private final String repoName = "DriverClegly";
    private final String branch = "main";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GithubDriveService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String uploadFile(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("Arquivo inválido");
        }

        String path = file.getName();
        String encodedPath = URLEncoder.encode(path, StandardCharsets.UTF_8);
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/contents/%s?ref=%s",
                repoOwner, repoName, encodedPath, branch);

        byte[] fileBytes = Files.readAllBytes(file.toPath());
        String base64Content = Base64.getEncoder().encodeToString(fileBytes);

        Map<String, Object> body = new HashMap<>();
        body.put("message", "upload " + path);
        body.put("content", base64Content);

        String jsonBody = objectMapper.writeValueAsString(body);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(githubToken);
        headers.set("Accept", "application/vnd.github+json");

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.PUT, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return "https://raw.githubusercontent.com/" + repoOwner + "/" + repoName + "/" + branch + "/" + path;
        } else {
            throw new IOException("Erro ao enviar arquivo para o GitHub: " + response.getStatusCode() + " - " + response.getBody());
        }
    }

    // novo: upload a partir de bytes para um path (ex: "123/perfil/123.png")
    public String uploadBytes(byte[] bytes, String path, String commitMessage) throws IOException {
        if (bytes == null || path == null || path.isBlank()) throw new IllegalArgumentException("bytes/path inválidos");

        // encode each path segment to preserve slashes
        String encodedPath = Arrays.stream(path.split("/"))
                .map(s -> URLEncoder.encode(s, StandardCharsets.UTF_8))
                .collect(Collectors.joining("/"));

        String apiUrl = String.format("https://api.github.com/repos/%s/%s/contents/%s?ref=%s",
                repoOwner, repoName, encodedPath, branch);

        String base64Content = Base64.getEncoder().encodeToString(bytes);

        Map<String, Object> body = new HashMap<>();
        body.put("message", commitMessage != null ? commitMessage : "upload " + path);
        body.put("content", base64Content);

        String jsonBody = objectMapper.writeValueAsString(body);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(githubToken);
        headers.set("Accept", "application/vnd.github+json");

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.PUT, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return "https://raw.githubusercontent.com/" + repoOwner + "/" + repoName + "/" + branch + "/" + path;
        } else {
            throw new IOException("Erro ao enviar arquivo para o GitHub: " + response.getStatusCode() + " - " + response.getBody());
        }
    }

    // conveniência: upload a partir de File para path
    public String uploadFile(File file, String path) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        return uploadBytes(bytes, path, "upload " + (path != null ? path : file.getName()));
    }
}
