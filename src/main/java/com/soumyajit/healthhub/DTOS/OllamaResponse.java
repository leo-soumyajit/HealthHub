package com.soumyajit.healthhub.DTOS;

import lombok.Data;

@Data
public class OllamaResponse {
    private String response;

    public OllamaResponse() {}

    public OllamaResponse(String response) {
        this.response = response;
    }
}
