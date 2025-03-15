package com.soumyajit.healthhub.Service;

import com.soumyajit.healthhub.model.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class AIChatService {

    @Value("${spring.ai.endpoint.base-url}")
    private String aiEndpoint;

    @Value("${spring.ai.model}")
    private String aiModel;

    private final WebClient webClient;

    public AIChatService(WebClient.Builder webClientBuilder) {
        // Build a web client without a base URL here, so we can append the endpoint path.
        this.webClient = webClientBuilder.build();
    }

    public ChatMessage getAIResponse(ChatMessage incomingMessage) {
        String userContent = incomingMessage.getContent().toLowerCase().trim();

        // Check for a simple greeting
        if (userContent.equals("hi") || userContent.equals("hello")) {
            return new ChatMessage("AI", "Hello! How can I help you today?", ChatMessage.MessageType.AI_RESPONSE);
        }

        // Otherwise, construct a detailed prompt for a structured JSON response
        String prompt = "You are HealthGuru, a smart AI health advisor. " +
                "When a user asks for help, provide a detailed, step-by-step solution in the following JSON format:\n\n" +
                "{\n" +
                "  \"title\": \"<Solution Title>\",\n" +
                "  \"steps\": [\n" +
                "    {\n" +
                "      \"step\": 1,\n" +
                "      \"instruction\": \"<Instruction 1>\",\n" +
                "      \"note\": \"<Additional note if any>\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"step\": 2,\n" +
                "      \"instruction\": \"<Instruction 2>\",\n" +
                "      \"note\": \"<Additional note if any>\"\n" +
                "    }\n" +
                "    // ... additional steps as needed\n" +
                "  ],\n" +
                "  \"note\": \"<Overall note or disclaimer>\"\n" +
                "}\n\n" +
                "Provide only the JSON response. \n" +
                "User: " + incomingMessage.getContent();

        try {
            Map<String, Object> payload = Map.of(
                    "model", aiModel,
                    "prompt", prompt,
                    "max_tokens", 400,
                    "stream", false
            );

            Map<String, Object> result = webClient.post()
                    .uri(aiEndpoint + "/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            String aiReply = result != null && result.get("response") != null
                    ? result.get("response").toString()
                    : "I'm sorry, I cannot provide a solution at this time.";

            return new ChatMessage("AI", aiReply, ChatMessage.MessageType.AI_RESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
            return new ChatMessage("AI", "There was an error processing your request.", ChatMessage.MessageType.AI_RESPONSE);
        }
    }

}
