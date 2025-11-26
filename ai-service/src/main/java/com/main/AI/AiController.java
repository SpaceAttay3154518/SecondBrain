package com.main.AI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AiController {

    private static final String GROQ_API_KEY = Config.get("GROQ_API_KEY");
    private static final String MODEL_NAME = Config.get("MODEL_NAME");
    public QueryManager qm;

    public AiController() {

        qm = new QueryManager(GROQ_API_KEY, MODEL_NAME);

    }


    // ---------------------------
    // 1. /api/fileUpload
    // ---------------------------
    @PostMapping("/fileUpload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {

        String randomId = UUID.randomUUID().toString();

        // TODO: save file via service using randomId (not implemented here)

        Map<String, Object> response = new HashMap<>();
        response.put("id", randomId);
        response.put("fileName", file.getOriginalFilename());
        response.put("status", "uploaded");

        return ResponseEntity.ok(response);
    }

    // ---------------------------
    // 2. /api/chat
    // ---------------------------
    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody ChatRequest request) {
        String id = request.getId();
        String msg = request.getQuestion();

        String res = qm.answerQuery(msg);

        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("reply", "The AI said: " + res);

        return ResponseEntity.ok(response);
    }

    // ---------------------------
    // 3. /deleteDB
    // ---------------------------
    @DeleteMapping("/deleteDB")
    public ResponseEntity<Map<String, Object>> deleteDB(@RequestBody ChatRequest request) {

        // TODO: actual deletion logic

        Map<String, Object> response = new HashMap<>();
        response.put("id", request.getId());
        response.put("status", "deleted");

        return ResponseEntity.ok(response);
    }

    // ------------ DTO ------------
    public static class ChatRequest {
        private String id;
        private String question;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
    }
}
