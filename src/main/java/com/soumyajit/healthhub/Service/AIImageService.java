package com.soumyajit.healthhub.Service;

import com.soumyajit.healthhub.model.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIImageService {

    // Make sure this property is set to "http://localhost:7000" in your application.properties
    @Value("${spring.ai.endpoint.base-url}")
    private String aiEndpoint;

    @Value("${spring.ai.model}")
    private String aiModel;

    private final WebClient webClient;

    public AIImageService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Processes an uploaded image for AI analysis.
     * The image is converted to Base64 and sent to the AI endpoint.
     * The AI is expected to return a JSON response with diagnosis and treatment suggestions.
     */
    public ChatMessage analyzeImage(MultipartFile imageFile) {
        try {
            // Convert image to Base64 string
            String base64Image = Base64.getEncoder().encodeToString(imageFile.getBytes());

            // Construct the prompt with a structured JSON format.
            String prompt = "You are HealthGuru, an AI health advisor. Analyze the following medical image (Base64 encoded) and provide a detailed, step-by-step solution for a possible diagnosis and treatment options, including a quick homemade solution for immediate relief. " +
                    "Strictly follow this JSON format:\n\n" +
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
                    "Ensure the output is valid JSON without extra text. " +
                    "Image (Base64): " + base64Image;

            // Prepare the payload
            Map<String, Object> payload = Map.of(
                    "model", aiModel,
                    "prompt", prompt,
                    "max_tokens", 600,
                    "stream", false
            );

            // Call the AI endpoint for image analysis.
            // IMPORTANT: The URI is constructed by concatenating the base URL with the image endpoint.
            Map<String, Object> result = webClient.post()
                    .uri(aiEndpoint + "/api/chat/image")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            String aiReply = (result != null && result.get("response") != null)
                    ? cleanJsonResponse(result.get("response").toString())
                    : getDefaultErrorResponse();

            return new ChatMessage("AI", aiReply, ChatMessage.MessageType.AI_RESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
            return new ChatMessage("AI", getDefaultErrorResponse(), ChatMessage.MessageType.AI_RESPONSE);
        }
    }

    /**
     * Extracts the first complete JSON object from the response.
     */
    private String cleanJsonResponse(String response) {
        Pattern pattern = Pattern.compile("\\{.*?\\}(?=\\s*\\{|$)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);
        return matcher.find() ? matcher.group(0) : getDefaultErrorResponse();
    }

    /**
     * Provides a default error JSON string.
     */
    private String getDefaultErrorResponse() {
        return "{\"title\": \"Error\", \"steps\": [{\"step\": 1, \"instruction\": \"AI could not generate a response for the image.\", \"note\": \"Please try again later.\"}], \"quick_relief\": \"\", \"note\": \"Apologies for the inconvenience.\"}";
    }
}
