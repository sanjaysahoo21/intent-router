package com.example.IntentRouter.model;

public record LogEntry(String intent, double confidence, String userMessage, String finalResponse) {

}
