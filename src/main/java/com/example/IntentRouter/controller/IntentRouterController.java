package com.example.IntentRouter.controller;

import com.example.IntentRouter.model.IntentClassification;
import com.example.IntentRouter.model.LogEntry;
import com.example.IntentRouter.model.UserRequest;
import com.example.IntentRouter.service.AuditLogger;
import com.example.IntentRouter.service.IntentRouterService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class IntentRouterController {

    private final IntentRouterService routerService;
    private final AuditLogger auditLogger;

    public IntentRouterController(IntentRouterService routerService, AuditLogger auditLogger) {
        this.routerService = routerService;
        this.auditLogger = auditLogger;
    }

    @PostMapping("/route")
    public ResponseEntity<Map<String, Object>> routeMessage(@RequestBody UserRequest request) {

        // 1. Classify the Intent using the first LLM call
        IntentClassification classification = routerService.classifyIntent(request.prompt());

        // 2. Generate the appropriate response using the second LLM call, or handle the
        // unclear fallback
        String responseText = routerService.routeAndRespond(request, classification);

        // 3. Audit Logging to route_log.jsonl
        LogEntry logEntry = new LogEntry(
                classification.intent(),
                classification.confidence(),
                request.prompt(),
                responseText);
        auditLogger.logRequest(logEntry);

        // 4. Return the result to the user as a JSON map
        return ResponseEntity.ok(Map.of(
                "intent", classification.intent(),
                "confidence", classification.confidence(),
                "response", responseText));
    }
}
