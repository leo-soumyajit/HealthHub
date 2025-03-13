// File: src/main/java/com/soumyajit/healthhub/Service/AIIntegrationService.java
package com.soumyajit.healthhub.Service;

import com.soumyajit.healthhub.DTOS.OllamaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class AIIntegrationService {

    @Value("${spring.ai.model}")
    private String model;

    private final WebClient webClient;

    public AIIntegrationService(WebClient.Builder webClientBuilder,
                                @Value("${spring.ai.endpoint.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public String getMealPlanSuggestion(String dietaryRestriction, String ingredients) {
        String prompt = "Generate a meal plan for a user with dietary restrictions: "
                + (dietaryRestriction != null ? dietaryRestriction : "none") + ".";
        if (ingredients != null && !ingredients.trim().isEmpty()) {
            prompt += " Available ingredients: " + ingredients + ".";
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", model);
        payload.put("prompt", prompt);
        payload.put("max_tokens", 150);
        payload.put("stream", false);

        String response = webClient.post()
                .uri("/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(OllamaResponse.class)
                .map(OllamaResponse::getResponse)
                .block();

        return response;
    }
}