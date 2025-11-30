package com.main.AI;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileWriter;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AiController {

    private static final String GROQ_API_KEY = Config.get("GROQ_API_KEY");
    private static final String MODEL_NAME = Config.get("MODEL_NAME");
    private static final String UPLOAD_DIR = "./src/main/uploads/";
    public QueryManager qm;

    public AiController() {

        qm = QueryManager.getInstance(GROQ_API_KEY, MODEL_NAME);

    }


    @PostMapping("/fileUpload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("id") String id) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            if (id == null || id.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("ID is required");
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return ResponseEntity.badRequest().body("Invalid file name");
            }

            String fileName = originalFilename.toLowerCase();
            boolean success = qm.parseDocument(file.getBytes(), originalFilename);
            if (!success) {
                return ResponseEntity.badRequest()
                        .body("Unsupported file type. Only .txt, .md, or .pdf are allowed.");
            }

            qm.getDb().saveToFile("./src/main/resources/dbs/" + id + ".db");

            return ResponseEntity.ok()
                    .body(new ChunkResponse(
                            originalFilename,
                            file.getSize()
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file: " + e.getMessage());
        }
    }

    @PostMapping("/query")
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
        String id = request.getId();
        String msg = request.getQuestion();

        String filepath = "./src/main/resources/dbs/" + id + ".db";
        Path path = Paths.get(filepath);
        if (!Files.exists(path)) {

            qm.getDb().saveToFile(filepath);

        }

        qm.getDb().loadFromFile(filepath);

        String res = qm.answerQuery(msg);

        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("reply", res);

        return ResponseEntity.ok(res);
    }


    @DeleteMapping("/deleteDB")
    public ResponseEntity<Map<String, Object>> deleteDB(@RequestBody ChatRequest request) {

       String filepath = "./src/main/resources/dbs/" + request.getId() + ".db";

       File file = new File(filepath);

        Map<String, Object> response = new HashMap<>();
        response.put("id", request.getId());

        if (file.delete()) {

            response.put("status", "deleted");

        } else {

            response.put("status", "Failed");

        }
        qm.getDb().clear();


        return ResponseEntity.ok(response);
    }

    @PostMapping("/documents")
    public ResponseEntity<String> getDocuments(@RequestBody ChatRequest request) throws JsonProcessingException {
        
        qm.getDb().loadFromFile("./src/main/resources/dbs/" + request.getId() + ".db");
        List<String> docs = qm.getDb().retrieveDocuments(); 
        Map<String, Object> response = new HashMap<>();
        response.put("id", request.getId());


        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(docs);
        return ResponseEntity.ok(json);

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

    static class ChunkResponse {
        private String filename;
        private long fileSize;

        public ChunkResponse(String filename, long fileSize) {
            this.filename = filename;
            this.fileSize = fileSize;
        }

        public String getFilename() { return filename; }
        public long getFileSize() { return fileSize; }
    }
}
