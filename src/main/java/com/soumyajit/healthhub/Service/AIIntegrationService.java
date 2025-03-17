// File: src/main/java/com/soumyajit/healthhub/Service/AIIntegrationService.java
package com.soumyajit.healthhub.Service;

import com.soumyajit.healthhub.DTOS.OllamaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;   

@Slf4j
@Service
public class AIIntegrationService {

    @Value("${spring.ai.model}")
    private String model;

    private final WebClient webClient;

    public AIIntegrationService(WebClient.Builder webClientBuilder,
                                @Value("${spring.ai.endpoint.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public String getMealPlanSuggestion(String dietaryRestriction, String ingredients, String healthGoal) {
        String prompt = "Generate a structured meal plan for a full week (Monday to Sunday). " +
                "Each day must include Breakfast, Lunch, and Dinner. " +
                "Dietary restrictions: " + (dietaryRestriction != null ? dietaryRestriction : "none") + ". ";
        if (ingredients != null && !ingredients.trim().isEmpty()) {
            prompt += "Available ingredients: " + ingredients + ". ";
        }
        if (healthGoal != null && !healthGoal.trim().isEmpty()) {
            prompt += "Health goal: " + healthGoal + ".";
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", model);
        payload.put("prompt", prompt);
        payload.put("max_tokens", 500);  // Increased token limit
        payload.put("stream", false);

        String response = webClient.post()
                .uri("/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(OllamaResponse.class)
                .map(OllamaResponse::getResponse)
                .block();

        log.info("🔹 AI Response: {}", response); // Log full response

        return response;
    }
}
