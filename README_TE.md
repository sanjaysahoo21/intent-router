# Intent-Based AI Router 🚀 (Tenglish Guide)

E project lo manam oka intelligent routing service ni build chesamu using **Java Spring Boot** and **Spring AI**. Deeni main purpose enti ante, user nunchi vacche questions ni ardham cheskuni (intent classification), aa specific topic lo expert aina AI persona ki route cheyadam.

## Architecture & Workflow (Idi ela work avtundi?)

Mana workflow lo main ga rendu LLM calls untayi:
1. **Intent Classification (LLM Call 1):** User adigina question (prompt) ni first LLM ki pampi, adi ye category ki sambandhinchindho kanukkuntam (e.g., `code`, `data`, `writing`, `career`, `unclear`). Idi mana system ki oka JSON format lo `{intent, confidence}` return chestundi.
2. **Expert Routing (LLM Call 2):** Vachina intent ni batti, `prompts.json` nunchi aa specific expert yokka system prompt ni teeskuni, malli LLM ki pamputham. Appudu aa expert AI accurate and detailed answer istundi.

Okavela user adigina question ardham kakapothe leda supported intent kakapothe, default ga `unclear` ani classify chesi, malli clear ga adagamani adugutundi (Fallback handling). Prathi request mariyu response ni auditing kosam `route_log.jsonl` lo save chesthamu.

---

## Files and Folders (Ekkada em undi & vaati purpose enti?)

### 1. **Controller Layer**
- **`IntentRouterController.java`**: Idi mana application ki entry point (REST API api). Ee file lo `/api/route` ane oka POST endpoint ni create chesamu.
  - *Methods:* 
    - `routeMessage()` -> User nunchi vacche JSON payload ni receive cheskuni, `IntentRouterService` nunchi classification and response thechkuni, final ga `AuditLogger` tho log chesi user ki return chestundi.

### 2. **Service Layer (Core Logic)**
- **`IntentRouterService.java`**: Indulo manam asalu AI interaction logic antha rasanu.
  - *`classifyIntent(String userMessage)`*: Ee method user prompt ni teeskuni, first LLM ki pampi classification chestundi. JSON format lo unna LLM response ni manam manual ga `ObjectMapper` use chesi parse chesthamu. Idi intentional ga design chesamu, endukante kondaru LLM providers direct Spring AI entity conversions ni support cheyaru.
  - *`routeAndRespond(UserRequest request, IntentClassification intentClassification)`*: Classification nunchi vacchina intent (like 'code', 'data') ni base cheskuni, `prompts.json` nunchi expert prompt laagi 2nd LLM call chestundi. Aa vacchina expert response ni return chestundi.
  
- **`AuditLogger.java`**:
  - *`logRoute()`*: Prathi user request, LLM classify chesina intent, vachina confidence, inka final answer ni teesukelli `route_log.jsonl` file lo JSON Lines format lo (oka line lo oka complete JSON object) append chestundi.

### 3. **Model Layer (Data Transfer Objects / Records)**
Eevi records ani pilustaru in Java 14+ (Immutable simple data classes).
- **`UserRequest.java`**: User pampinche JSON payload representation (e.g., `{"prompt": "help me code"}`).
- **`IntentClassification.java`**: 1st LLM nunchi vacche result ni map cheskodaniki vaade record. (Andulo `intent` inka `confidence` fields untayi).
- **`LogEntry.java`**: Mana `route_log.jsonl` lo log form chese data structure idhe.

### 4. **Resources & Configurations**
- **`prompts.json`**: Indulo different experts (code, data, writing, career) ki kavalsina system instructions/personas define chesamu.
- **`application.properties`**: API keys mariyu base URLs (Groq/OpenAI/Gemini kosam) configure cheyadaniki use chesamu. Free tier quotas twaraga exhaust avvakunda undadaniki `spring.ai.retry.max-attempts=1` ani kooda set chesamu.
- **`Dockerfile` & `docker-compose.yml`**: Application ni local machine lone kakunda, ekkadaina smoothly run cheyadaniki containerize chesamu.

---

## How to Run? (Ela run cheyali?)

### 1. Initial Setup
Mee system lo `.env` file create chesi, andulo API key ivvali (Examples `.env.example` lo unnayi):
```env
GROQ_API_KEY=your_groq_api_key_here
```

### 2. Run over Docker (Recommended & Easy approach)
Docker tho direct ga build chesi run cheyadaniki:
```bash
docker-compose up --build
```
Idi maven ni automatically download chesi, JAR file build chesi, application start chestundi.

### 3. Run Locally (Without Docker)
Oka vela meku local terminal nunchi run cheyalante:
```bash
./mvnw spring-boot:run
```

---

## Test Cheyadam Ela?

App successfully run ayyaka, inko terminal open chesi kinda cURL command tho test cheyochu:

**Coding kosam adigithe:**
```bash
curl -s -X POST http://localhost:8080/api/route \
-H "Content-Type: application/json" \
-d '{"prompt": "how do i sort a list of objects in python?"}'
```
*Appudu neat ga code expert respond ayyi meku Python snippets parameter tho saha istundi.*

**Career kosam adigithe:**
```bash
curl -s -X POST http://localhost:8080/api/route \
-H "Content-Type: application/json" \
-d '{"prompt": "How do I prepare for a software engineering interview?"}'
```
*Ippudu idi `career` lane classify ayyi, manchi interview tips istundi.*
