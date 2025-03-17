package com.soumyajit.healthhub.Service;

import com.soumyajit.healthhub.model.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIChatService {

    @Value("${spring.ai.endpoint.base-url}")
    private String aiEndpoint;

    @Value("${spring.ai.model}")
    private String aiModel;

    private final WebClient webClient;

    public AIChatService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public ChatMessage getAIResponse(ChatMessage incomingMessage) {
        String userContent = incomingMessage.getContent().trim();

        // Check for a simple greeting
        if (userContent.equalsIgnoreCase("hi") || userContent.equalsIgnoreCase("hello")) {
            return new ChatMessage("AI",
                    "{\"title\": \"Greeting\", \"steps\": [{\"step\": 1, \"instruction\": \"Hello! How can I assist you today?\", \"note\": \"Ask me anything health-related.\"}], \"quick_relief\": \"\", \"note\": \"I'm here to help!\"}",
                    ChatMessage.MessageType.AI_RESPONSE);
        }

        // Construct structured JSON prompt with at least 4 steps and a quick relief field
        String prompt = "You are HealthGuru, an AI health advisor. " +
                "Provide a detailed, step-by-step solution in JSON format only. " +
                "Your response must include at least four steps and a quick homemade solution for immediate relief. " +
                "Strictly follow this format:\n\n" +
                "{\n" +
                "  \"title\": \"<Solution Title>\",\n" +
                "  \"steps\": [\n" +
                "    { \"step\": 1, \"instruction\": \"<Instruction 1>\", \"note\": \"<Additional note if any>\" },\n" +
                "    { \"step\": 2, \"instruction\": \"<Instruction 2>\", \"note\": \"<Additional note if any>\" },\n" +
                "    { \"step\": 3, \"instruction\": \"<Instruction 3>\", \"note\": \"<Additional note if any>\" },\n" +
                "    { \"step\": 4, \"instruction\": \"<Instruction 4>\", \"note\": \"<Additional note if any>\" }\n" +
                "  ],\n" +
                "  \"quick_relief\": \"<Quick homemade solution for immediate relief>\",\n" +
                "  \"note\": \"<Overall note or disclaimer>\"\n" +
                "}\n\n" +
                "Ensure the output is valid JSON without extra text. \n" +
                "User Query: " + userContent;

        try {
            Map<String, Object> payload = Map.of(
                    "model", aiModel,
                    "prompt", prompt,
                    "max_tokens", 600,
                    "stream", false
            );

            Map<String, Object> result = webClient.post()
                    .uri(aiEndpoint + "/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            String aiReply = (result != null && result.get("response") != null)
                    ? cleanJsonResponse(result.get("response").toString())
                    : "{\"title\": \"Error\", \"steps\": [{\"step\": 1, \"instruction\": \"AI could not generate a response.\", \"note\": \"Please try again later.\"}], \"quick_relief\": \"\", \"note\": \"Apologies for the inconvenience.\"}";

            return new ChatMessage("AI", aiReply, ChatMessage.MessageType.AI_RESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
            return new ChatMessage("AI",
                    "{\"title\": \"Error\", \"steps\": [{\"step\": 1, \"instruction\": \"An error occurred while processing your request.\", \"note\": \"Try again later.\"}], \"quick_relief\": \"\", \"note\": \"Apologies for the inconvenience.\"}",
                    ChatMessage.MessageType.AI_RESPONSE);
        }
    }

    // Ensure JSON is properly extracted
    private String cleanJsonResponse(String response) {
        Pattern pattern = Pattern.compile("\\{.*\\}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);
        return matcher.find() ? matcher.group(0)
                : "{\"title\": \"Invalid Response\", \"steps\": [{\"step\": 1, \"instruction\": \"AI response was not in JSON format.\", \"note\": \"Please check with the developers.\"}], \"quick_relief\": \"\", \"note\": \"Technical error.\"}";
    }
}