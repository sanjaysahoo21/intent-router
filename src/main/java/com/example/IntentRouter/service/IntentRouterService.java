package com.example.IntentRouter.service;

import java.io.IOException;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import com.example.IntentRouter.model.IntentClassification;
import com.example.IntentRouter.model.UserRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;

@Service
public class IntentRouterService {

    private final ChatClient chatClient;
    private final Map<String, String> systemPrompts;

    public IntentRouterService(ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper,
            @Value("classpath:prompts.json") Resource promptsResource) throws IOException {
        this.chatClient = chatClientBuilder.build();
        this.systemPrompts = objectMapper.readValue(promptsResource.getInputStream(),
                new TypeReference<Map<String, String>>() {
                });
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

    public String routeAndRespond(UserRequest request, IntentClassification intentClassification) {

        String intent = intentClassification.intent().toLowerCase();

        if (intent.equals("unclear") || !systemPrompts.containsKey(intent)) {
            return "Could you please clarify? I can help you with coding, data, analysis, writing, or career advice.";
        }

        String expertSystemPrompt = systemPrompts.get(intent);

        return chatClient.prompt()
                .system(expertSystemPrompt)
                .user(request.prompt())
                .call()
                .content();
    }

}
