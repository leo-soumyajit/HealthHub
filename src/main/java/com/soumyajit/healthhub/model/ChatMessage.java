package com.soumyajit.healthhub.model;

import java.time.LocalDateTime;

public class ChatMessage {
    private String sender;
    private String content;
    private MessageType type;
    private LocalDateTime timestamp = LocalDateTime.now();

    public enum MessageType {
        CHAT,
        IMAGE,
        AI_RESPONSE
    }

    public ChatMessage() {}

    public ChatMessage(String sender, String content, MessageType type) {
        this.sender = sender;
        this.content = content;
        this.type = type;
    }

    // Getters and setters...
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}