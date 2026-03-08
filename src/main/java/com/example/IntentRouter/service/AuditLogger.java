package com.example.IntentRouter.service;

import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.example.IntentRouter.model.LogEntry;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

@Service
public class AuditLogger {

    private final ObjectMapper objectMapper;
    private final Path logFilePath;

    public AuditLogger(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.logFilePath = Paths.get("route_log.jsonl");
    }

    public void logRequest(LogEntry entry) {

        try {
            
            String jsonLine = objectMapper.writeValueAsString(entry) + System.lineSeparator();

            Files.writeString(
                logFilePath,
                jsonLine,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );

        } catch (IOException e) {
            System.err.println("Failed to log request: " + e.getMessage());
        }
    }

}
