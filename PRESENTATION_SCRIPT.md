# 🎬 Intent-Based AI Router: Video Presentation Script

This guide is designed to help you record a professional, YouTube-style walkthrough of your project for your mentor. It tells you exactly **where to start**, **what to show on screen**, and **what to say** at each step.

---

## 🕒 Phase 1: The Hook & Introduction (0:00 - 1:00)
**On Screen:** Show the `README.md` file or a simple diagram of your architecture. Have your terminal open in the background.

**What to Say:**
> "Hey everyone! Today I want to walk you through a really cool project I recently built. It's an **Intent-Based AI Prompt Router** created using **Java Spring Boot**, **Spring AI**, and the **Groq Llama 3.1 model**."

> "The core problem this solves is pretty common in AI apps: If you ask a generic AI a coding question, it might give you an okay answer. But if you tell the AI *'You are a senior Principal Engineer'* before asking, the answer gets 10x better. So, my system automatically figures out *what* the user is asking (the intent), and routes their question to a specialized AI 'expert' persona (like a coding expert, a data analyst, or a career coach). Let me show you how it works!"

---

## 💻 Phase 2: Live Demonstration First (1:00 - 3:00)
**On Screen:** Switch to your Terminal. Make sure your Docker container or Spring Boot app is running (`docker-compose up` or `./mvnw spring-boot:run`). Open a second terminal window to run `curl` commands.

**What to Say:**
> "Before diving into the code, let's see it in action. I expose a simple REST endpoint at `/api/route`. Let me send a coding question using `cURL`."

*(Run this command on screen)*
```bash
curl -s -X POST http://localhost:8080/api/route -H "Content-Type: application/json" -d '{"prompt": "how do i sort a list of objects in python?"}'
```

> "Look at the response! As you can see, the JSON returns an `intent` labeled as `code`, a high `confidence` score, and a highly detailed Python coding response. Behind the scenes, the AI actually shifted into a 'Senior Software Engineer' persona to write this."

> "Now, what if I ask something vague?"

*(Run this command on screen)*
```bash
curl -s -X POST http://localhost:8080/api/route -H "Content-Type: application/json" -d '{"prompt": "hello"}'
```

> "Notice how the `intent` came back as `unclear`. Instead of wasting API credits trying to guess what I meant, my router implemented a graceful fallback. It intercepted the request and asked me to clarify. This saves tokens and keeps the user experience focused."

---

## 🧠 Phase 3: The Architecture & Code Walkthrough (3:00 - 6:00)
**On Screen:** Open your IDE (VS Code or IntelliJ). Open `IntentRouterController.java`.

**What to Say:**
> "So, how did I build this? Let's look at the code. The entry point is my `IntentRouterController`, which accepts a POST request and maps the JSON to a simple Java Record called `UserRequest`. By using Java Records, I completely eliminated traditional DTO boilerplate code."

**On Screen:** Switch to `IntentRouterService.java`. Slowly scroll down as you explain.

> "The real magic happens in the `IntentRouterService`. This is the heart of the application. I'm using Spring AI's `ChatClient` because it acts as a universal remote—it abstracts away the provider so I can easily swap between OpenAI, Gemini, or Groq just by changing `.env` variables without touching Java code."

> "Here is my 2-step LLM process:"

**(Highlight the `classifyIntent` method)**
> "Step 1: The Classifier. I send a strict system prompt to the LLM asking it to classify the user's message into exactly one of five labels: code, data, writing, career, or unclear. I instruct it to return ONLY raw JSON. Because LLMs sometimes accidentally wrap output in Markdown code blocks (like ` ```json `), I wrote a robust cleanup block here to strip out markdown before parsing it into my `IntentClassification` record using Jackson's `ObjectMapper`. This makes the parsing practically fail-proof."

**(Highlight the `routeAndRespond` method)**
> "Step 2: The Router. Once I have the intent, I check my `prompts.json` file, which holds the different expert personas. If the intent is `unclear`, I short-circuit and return a fallback message. If it's valid, I grab the specialized prompt (like the Data Analyst persona) and fire off a second LLM request. The response is highly tailored."

---

## 📝 Phase 4: Audit Logging & Operations (6:00 - 7:30)
**On Screen:** Open `AuditLogger.java`, then open `route_log.jsonl`.

**What to Say:**
> "In production systems, observability is critical. I created an `AuditLogger` service that intercepts the final response and writes everything to a `.jsonl` (JSON Lines) file."

> "If we look at `route_log.jsonl` here, you can see every request, the classified intent, the confidence score, and the final output. I chose JSON Lines format because it's append-friendly and exceptionally easy to pipe into logging tools like ELK or Datadog."

**On Screen:** Open `docker-compose.yml` and `Dockerfile`.

> "Finally, I wanted this to be easy to deploy. I wrote a multi-stage Dockerfile using Maven to cache dependencies and build the JAR, then I package it into a super lightweight Alpine JRE image. With my `docker-compose.yml`, spinning up the entire infrastructure with the correct environment variables takes one command."

---

## 🎯 Phase 5: Conclusion & Reflection (7:30 - 8:30)
**On Screen:** Bring back your face/camera, or stay on the `README.md`.

**What to Say:**
> "To wrap up, this project was a fantastic deep dive into **LLM orchestration**. I learned how to deal with non-deterministic LLM outputs by forcing structured JSON, how to utilize Spring AI's Fluent API, and how to design a provider-agnostic system. The hardest challenge was handling rate limits on free AI tiers, which I solved by switching to Groq's high-speed API and managing Spring AI's internal retry mechanisms."

> "Thank you for watching! I'd love to hear your feedback on the architecture."

---

## 📌 Pro-Tips for Recording
1. **Zoom in your text:** Make sure your IDE font and Terminal font are zoomed in (at least 14pt-16pt). If the mentor can't read the code on YouTube, it ruins the video.
2. **Clear the Terminal:** Before recording, type `clear` in your terminal so it looks neat.
3. **Practice the Flow:** Do one "dry run" without recording just to get comfortable switching between Terminal and VS Code.
4. **Enthusiasm:** Speak with confidence. You built a real, production-ready AI pattern (Intent Routing) using modern tools (Records, Spring Boot 3.4, Multi-stage Docker). Own it!
