# AI Model Integration Guide

## Overview

This document describes how the AI Model integrates with the Spring Boot backend and Nuxt frontend.

## Architecture

```
┌─────────────────┐
│  Nuxt Frontend  │
│   Port 3000     │
└────────┬────────┘
         │ HTTP + Firebase Auth Token
         ↓
┌─────────────────┐
│ Spring Boot     │
│  Backend        │
│   Port 8080     │
└────────┬────────┘
         │ HTTP (Internal)
         ↓
┌─────────────────┐
│   AI Model      │
│   Service       │
│   Port 8081     │
└─────────────────┘
```

## Backend Components (✅ Implemented)

### 1. DTOs (Data Transfer Objects)

**`dto/QueryRequest.java`**
```java
{
  "question": "What is the capital of France?",
  "userId": "firebase-uid-123"
}
```

**`dto/QueryResponse.java`**
```java
{
  "answer": "The capital of France is Paris.",
  "success": true,
  "error": null
}
```

**`dto/DocumentUploadResponse.java`**
```java
{
  "success": true,
  "message": "Document uploaded successfully",
  "documentId": "doc-123"
}
```

### 2. AI Service (`service/AIService.java`)

Handles communication with the AI Model service:
- `askQuestion(question, userId)` - Forwards question to AI Model
- `uploadDocument(file, userId)` - Sends document to AI Model for processing
- `getUserDocuments(userId)` - Gets list of user's documents
- `deleteDocument(documentId, userId)` - Deletes a document

### 3. AI Controller (`controller/AIController.java`)

Exposes REST endpoints for the frontend:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/ai/ask` | POST | Ask a question |
| `/api/ai/document/upload` | POST | Upload PDF/TXT |
| `/api/ai/documents` | GET | List documents |
| `/api/ai/document/{id}` | DELETE | Delete document |

All endpoints are **protected by Firebase authentication**.

### 4. Configuration (`application.properties`)

```properties
ai.service.url=http://localhost:8081
spring.servlet.multipart.max-file-size=10MB
```

## Frontend Components (✅ Implemented)

### 1. useAI Composable (`composables/useAI.js`)

Provides easy-to-use functions for AI features:

```javascript
const { askQuestion, uploadDocument, getUserDocuments, deleteDocument } = useAI()

// Ask a question
const response = await askQuestion("What is RAG?")
console.log(response.answer)

// Upload a document
const result = await uploadDocument(file)
console.log(result.message)
```

### 2. Test Page (`app/pages/test-ai.vue`)

Demo page at `http://localhost:3000/test-ai`:
- Ask questions interface
- Document upload interface
- Shows authentication status

## AI Model Requirements (⏳ To Be Implemented)

Your AI Model needs to be converted to a **Spring Boot REST API** on **port 8081**.

### Required Endpoints:

#### 1. POST `/api/query`

**Purpose**: Answer questions using RAG

**Request:**
```json
{
  "question": "Where can I find Moroccan Traditional Food?",
  "userId": "firebase-uid-123"
}
```

**Response:**
```json
{
  "answer": "Based on the uploaded documents, Moroccan Traditional Food can be found...",
  "success": true
}
```

**Implementation Hint:**
```java
@PostMapping("/api/query")
public QueryResponse query(@RequestBody QueryRequest request) {
    // Use existing QueryManager
    QueryManager qm = new QueryManager(GROQ_API_KEY, MODEL_NAME);
    String answer = qm.answerQuery(request.getQuestion());
    
    return new QueryResponse(answer, true);
}
```

#### 2. POST `/api/document/upload`

**Purpose**: Upload and process documents

**Request:** Multipart form data
- `file`: PDF or TXT file
- `userId`: User ID

**Response:**
```json
{
  "success": true,
  "message": "Document processed successfully",
  "documentId": "doc-123"
}
```

**Implementation Hint:**
```java
@PostMapping("/api/document/upload")
public DocumentUploadResponse upload(
    @RequestParam("file") MultipartFile file,
    @RequestParam("userId") String userId) throws IOException {
    
    // Save file temporarily
    File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
    file.transferTo(tempFile);
    
    // Use existing QueryManager to process
    QueryManager qm = new QueryManager(GROQ_API_KEY, MODEL_NAME);
    
    if (file.getOriginalFilename().endsWith(".pdf")) {
        qm.parsePDF(tempFile.getAbsolutePath());
    } else {
        qm.parseTxt(tempFile.getAbsolutePath());
    }
    
    tempFile.delete();
    
    return new DocumentUploadResponse(true, "Document processed successfully");
}
```

#### 3. GET `/api/documents` (Optional)

**Purpose**: List user's uploaded documents

**Response:**
```json
[
  {
    "id": "doc-123",
    "filename": "document.pdf",
    "uploadedAt": "2025-11-26T10:00:00Z"
  }
]
```

#### 4. DELETE `/api/document/{documentId}` (Optional)

**Purpose**: Delete a document and its vectors

## How to Convert AI Model to Spring Boot

### Step 1: Add Spring Boot to `pom.xml`

Add these dependencies to `AiModel/pom.xml`:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.0</version>
</parent>

<dependencies>
    <!-- Keep existing langchain4j dependencies -->
    
    <!-- Add Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webmvc</artifactId>
    </dependency>
</dependencies>
```

### Step 2: Create Spring Boot Main Class

Replace `Main.java` with:

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AIModelApplication {
    public static void main(String[] args) {
        SpringApplication.run(AIModelApplication.class, args);
    }
}
```

### Step 3: Create REST Controller

Create `AIController.java`:

```java
@RestController
@RequestMapping("/api")
public class AIController {
    
    private final QueryManager queryManager;
    
    public AIController() {
        String apiKey = Config.get("GROQ_API_KEY");
        String modelName = Config.get("MODEL_NAME");
        this.queryManager = new QueryManager(apiKey, modelName);
    }
    
    @PostMapping("/query")
    public QueryResponse query(@RequestBody QueryRequest request) {
        String answer = queryManager.answerQuery(request.getQuestion());
        return new QueryResponse(answer, true);
    }
    
    @PostMapping("/document/upload")
    public DocumentUploadResponse upload(@RequestParam("file") MultipartFile file) {
        // Implementation here
    }
}
```

### Step 4: Configure Port

Create `src/main/resources/application.properties`:

```properties
server.port=8081
```

### Step 5: Run

```bash
cd AiModel
mvn spring-boot:run
```

The AI Model will now run as a REST API on `http://localhost:8081`

## Testing the Integration

### 1. Start All Services

**Terminal 1 - Backend:**
```bash
cd backend
mvn spring-boot:run
```

**Terminal 2 - AI Model:**
```bash
cd AiModel
mvn spring-boot:run
```

**Terminal 3 - Frontend:**
```bash
cd Secondbrain
npm run dev
```

### 2. Test Flow

1. Go to `http://localhost:3000/test-ai`
2. Login if needed
3. Upload a PDF document
4. Ask a question about the document
5. See the AI-generated answer

### 3. Expected Console Output

**Backend (8080):**
```
📝 Question from user abc123: Where is Moroccan food?
✅ Answer generated successfully
```

**AI Model (8081):**
```
Processing query: Where is Moroccan food?
Found 3 relevant documents
Generated answer using RAG
```

## Request Flow Example

### Complete Flow: User Asks a Question

1. **User action:** Types question in frontend

2. **Frontend (`useAI.js`):**
```javascript
const response = await askQuestion("Where is Moroccan food?")
```

3. **HTTP Request:**
```http
POST http://localhost:8080/api/ai/ask
Authorization: Bearer <firebase-token>
Content-Type: application/json

{
  "question": "Where is Moroccan food?"
}
```

4. **Backend (`AIController.java`):**
- Extracts UID from Firebase token
- Calls `AIService.askQuestion()`

5. **Backend (`AIService.java`):**
```http
POST http://localhost:8081/api/query
Content-Type: application/json

{
  "question": "Where is Moroccan food?",
  "userId": "abc123"
}
```

6. **AI Model (`AIController.java` in AI Model):**
- Uses `QueryManager.answerQuery()`
- Returns answer

7. **Response flows back:**
```
AI Model → Backend → Frontend → User sees answer
```

## Configuration

### Backend Configuration

File: `backend/src/main/resources/application.properties`

```properties
# AI Model service URL
ai.service.url=http://localhost:8081

# File upload limits
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### AI Model Configuration

File: `AiModel/src/main/resources/application.properties`

```properties
# Server port
server.port=8081

# GROQ API configuration
groq.api.key=${GROQ_API_KEY}
model.name=${MODEL_NAME}
```

## Error Handling

The backend handles errors gracefully:

- **AI Model not running:** Returns error message to frontend
- **Invalid file type:** Validates PDF/TXT only
- **Authentication failure:** Firebase filter returns 401
- **AI Model error:** Catches and returns error message

## Next Steps

1. ✅ **Backend ready** - All proxy endpoints implemented
2. ✅ **Frontend ready** - Composable and test page ready
3. ⏳ **AI Model** - Convert to Spring Boot REST API (your teammate)
4. 🔄 **Integration testing** - Test the full flow once AI Model is ready

## Questions?

Refer to:
- `backend/src/main/java/com/example/demo/controller/AIController.java` - Backend endpoints
- `backend/src/main/java/com/example/demo/service/AIService.java` - AI service calls
- `Secondbrain/composables/useAI.js` - Frontend usage
- `Secondbrain/app/pages/test-ai.vue` - Working example
