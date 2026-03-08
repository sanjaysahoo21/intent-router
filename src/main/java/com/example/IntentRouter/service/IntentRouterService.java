package com.example.IntentRouter.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import com.example.IntentRouter.model.IntentClassification;

@Service
public class IntentRouterService {

    private final ChatClient chatClient;

    public IntentRouterService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public IntentClassification classifyIntent(String userMessage) {
        
        String systemInstruction = """
            Your task is to classify the user's intent. 
            Choose exactly ONE of the following distinct labels: code, data, writing, career, unclear.
            """;

            try {
                
                return chatClient.prompt()
                        .system(systemInstruction)
                        .user(userMessage)
                        .call()
                        .entity(IntentClassification.class);

            } catch (Exception e) {
                return new IntentClassification("unclear", 0.0);
            }
        

    }

}
