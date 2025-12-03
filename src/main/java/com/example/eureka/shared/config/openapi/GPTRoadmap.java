package com.example.eureka.shared.config.openapi;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GPTRoadmap {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${open-ai-api-key:}")
    private String apiKey;

    @Value("${open-ai-api-url:}")
    private String apiUrl;
    public String generarRoadmap(String historia, String objetivo) {

        String prompt = """
            Genera un roadmap de 5 pasos para un emprendimiento.
            Pasos: Idea, Validación, Planificación, Desarrollo, Lanzamiento.
            Historia: %s
            Objetivo: %s

            Responde exclusivamente en JSON con el formato:
            [
                {"fase": "Idea", "descripcion": "..."},
                {"fase": "Validación", "descripcion": "..."},
                {"fase": "Planificación", "descripcion": "..."},
                {"fase": "Desarrollo", "descripcion": "..."},
                {"fase": "Lanzamiento", "descripcion": "..."}
            ]
        """.formatted(historia, objetivo);

        Map<String, Object> request = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map<String, Object> body = response.getBody();
        List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
        Map<String, Object> firstChoice = choices.get(0);
        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
        String content = (String) message.get("content");

        return content;

    }
}
