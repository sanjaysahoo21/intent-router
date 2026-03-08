package com.example.IntentRouter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LogEntry(
        String intent,
        double confidence,
        @JsonProperty("user_message") String userMessage,
        @JsonProperty("final_response") String finalResponse) {

}
