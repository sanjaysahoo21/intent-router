# Intent-Based AI Router

An intelligent request routing service built with **Java Spring Boot** and **Spring AI** that classifies user intent and routes requests to specialized AI expert personas.

## Architecture

```
User Request → [REST API] → [Intent Classifier (LLM Call #1)]
                                    ↓
                            IntentClassification {intent, confidence}
                                    ↓
                    ┌───────────────┼───────────────┐
                    ↓               ↓               ↓
              [Code Expert]   [Data Expert]   [Writing Expert] ...
                    ↓               ↓               ↓
              [LLM Call #2 with specialized system prompt]
                                    ↓
                            Final Response + Audit Log
```

### How It Works

1. **Intent Classification (LLM Call #1):** A lightweight prompt asks the LLM to classify the user's message into one of four categories: `code`, `data`, `writing`, `career`, or `unclear`. The response is a structured JSON object with `intent` and `confidence` fields.

2. **Expert Routing (LLM Call #2):** Based on the classified intent, the router selects a specialized system prompt from `prompts.json` and makes a second LLM call with the expert persona context.

3. **Fallback Handling:** If the intent is `unclear` or the classifier fails, the system returns a clarifying question without making a second LLM call.

4. **Audit Logging:** Every interaction is logged to `route_log.jsonl` in JSON Lines format, capturing the intent, confidence, user message, and final response.

## Tech Stack

- **Java 17** + **Spring Boot 3.4.3**
- **Spring AI 1.0.0-M5** (OpenAI-compatible client)
- **Groq API** (running Llama 3.1 — free tier, OpenAI-compatible)
- **Maven** for build management
- **Docker** for containerization

## Project Structure

```
src/main/java/com/example/IntentRouter/
├── IntentRouterApplication.java      # Spring Boot entry point
├── controller/
│   └── IntentRouterController.java   # REST endpoint: POST /api/route
├── model/
│   ├── UserRequest.java              # Input DTO (record)
│   ├── IntentClassification.java     # Classifier output (record)
│   └── LogEntry.java                 # Audit log structure (record)
└── service/
    ├── IntentRouterService.java       # Core: classify + route logic
    └── AuditLogger.java              # JSON Lines file logger
```

## Setup Instructions

### Prerequisites

- Java 17+
- Maven 3.9+ (or use the included Maven Wrapper `./mvnw`)
- Docker & Docker Compose (for containerized deployment)
- An API key from one of the supported LLM providers

### 1. Clone the Repository

```bash
git clone <repository-url>
cd IntentRouter
```

### 2. Configure Environment Variables

```bash
cp .env.example .env
```

Edit `.env` and add your API key:

```
GROQ_API_KEY=gsk_your_actual_key_here
```

> **Supported Providers:** This application uses an OpenAI-compatible API format. You can use:
> - **Groq** (recommended, free): Get a key at [console.groq.com](https://console.groq.com)
> - **OpenAI**: Update `application.properties` to use `https://api.openai.com` as the base URL
> - **Google Gemini**: Update `application.properties` to use `https://generativelanguage.googleapis.com/v1beta/openai/`

### 3. Run with Docker (Recommended)

```bash
docker-compose up --build
```

The service will be available at `http://localhost:8080`.

### 4. Run Locally (Alternative)

```bash
./mvnw spring-boot:run
```

## API Usage

### Endpoint

```
POST /api/route
Content-Type: application/json
```

### Request Body

```json
{
  "prompt": "How do I sort a list of objects in Python?"
}
```

### Response

```json
{
  "intent": "code",
  "confidence": 0.95,
  "response": "To sort a list of objects in Python, you can use..."
}
```

### Example Requests

```bash
# Code intent
curl -s -X POST http://localhost:8080/api/route \
  -H "Content-Type: application/json" \
  -d '{"prompt": "Write a python function to reverse a linked list"}'

# Data intent
curl -s -X POST http://localhost:8080/api/route \
  -H "Content-Type: application/json" \
  -d '{"prompt": "Analyze the sales data and find the trend"}'

# Writing intent
curl -s -X POST http://localhost:8080/api/route \
  -H "Content-Type: application/json" \
  -d '{"prompt": "Help me write a professional email to my manager"}'

# Career intent
curl -s -X POST http://localhost:8080/api/route \
  -H "Content-Type: application/json" \
  -d '{"prompt": "How do I prepare for a software engineering interview?"}'

# Unclear intent (triggers fallback)
curl -s -X POST http://localhost:8080/api/route \
  -H "Content-Type: application/json" \
  -d '{"prompt": "hello"}'
```

## Design Decisions

### Why Spring AI with OpenAI-Compatible Client?

Spring AI's OpenAI client can target **any OpenAI-compatible endpoint** by changing the `base-url` property. This makes the application **provider-agnostic** — switching from Groq to OpenAI to Gemini requires only a config change, zero code changes.

### Why Manual JSON Parsing Instead of `.entity()`?

Spring AI's `.entity()` method relies on structured output features that not all OpenAI-compatible providers support. By using `.content()` + Jackson `ObjectMapper`, we ensure:
- Compatibility across all LLM providers
- Graceful handling of malformed JSON (markdown code fences, extra text)
- Explicit error handling with fallback to `"unclear"` intent

### Why JSON Lines for Logging?

JSON Lines (`.jsonl`) format is:
- Append-friendly (no need to parse the entire file to add entries)
- Easy to process with standard tools (`jq`, `grep`)
- Each line is independently valid JSON

## Configuration

Key properties in `src/main/resources/application.properties`:

| Property | Description | Default |
|---|---|---|
| `spring.ai.openai.api-key` | LLM provider API key | `${GROQ_API_KEY}` |
| `spring.ai.openai.base-url` | OpenAI-compatible endpoint | `https://api.groq.com/openai` |
| `spring.ai.openai.chat.options.model` | Model to use | `llama-3.1-8b-instant` |
| `spring.ai.retry.max-attempts` | Max retry attempts | `1` |

## Log File Format

Each line in `route_log.jsonl` is a JSON object:

```json
{"intent":"code","confidence":0.95,"userMessage":"how to sort in python?","finalResponse":"You can use sorted()..."}
```

## License

This project was built as a learning exercise for intent-based AI routing with Spring AI.
