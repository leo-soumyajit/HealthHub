package com.soumyajit.healthhub.Controller;

import com.soumyajit.healthhub.model.ChatMessage;
import com.soumyajit.healthhub.Service.AIChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @Autowired
    private AIChatService aiChatService;

    // This method handles messages sent to /app/chat.sendMessage
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage chatMessage) {
        System.out.println("ChatController: Received message from "
                + chatMessage.getSender() + ": " + chatMessage.getContent());
        // If the message type is CHAT, generate an AI response.
        if (chatMessage.getType() == ChatMessage.MessageType.CHAT) {
            ChatMessage aiResponse = aiChatService.getAIResponse(chatMessage);
            System.out.println("ChatController: Sending AI response: " + aiResponse.getContent());
            return aiResponse;
        }
        // Otherwise, just return the original message.
        return chatMessage;
    }
}
