package com.main.AI;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;

import java.util.ArrayList;
import java.util.List;

public class RagService {

    private final VectorDbManager vectorDb;
    private final ChatLanguageModel chatModel;
    private final String systemPrompt;

    public RagService(VectorDbManager vectorDb,
                      ChatLanguageModel chatModel) {
        this.vectorDb = vectorDb;
        this.chatModel = chatModel;
        this.systemPrompt = "You are a helpful assistant.";
    }

 
    public List<String> chunkText(String text) {
        final int MAX_CHARS = 1000; // change as needed

        List<String> chunks = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) return chunks;

        // 1) Split into paragraphs by two or more newlines (preserves single newlines inside)
        String[] paragraphs = text.split("(\\r?\\n){2,}");

        for (String para : paragraphs) {
            String p = para.trim();
            if (p.isEmpty()) continue;

            // If short, add directly
            if (p.length() <= MAX_CHARS) {
                chunks.add(p);
                continue;
            }

            // 2) Split paragraph into sentences using a simple regex.
            // This keeps punctuation on the sentence and splits on whitespace after ., ! or ?
            String[] sentences = p.split("(?<=[.!?])\\s+");

            StringBuilder current = new StringBuilder();
            for (String s : sentences) {
                String sent = s.trim();
                if (sent.isEmpty()) continue;

                // If a single sentence is longer than MAX_CHARS, chunk it by characters
                if (sent.length() > MAX_CHARS) {
                    // flush existing
                    if (current.length() > 0) {
                        chunks.add(current.toString().trim());
                        current.setLength(0);
                    }
                    // split this long sentence into pieces of MAX_CHARS
                    int idx = 0;
                    while (idx < sent.length()) {
                        int end = Math.min(idx + MAX_CHARS, sent.length());
                        chunks.add(sent.substring(idx, end).trim());
                        idx = end;
                    }
                    continue;
                }

                // If adding this sentence would exceed MAX_CHARS, flush current chunk
                if (current.length() > 0 && current.length() + 1 + sent.length() > MAX_CHARS) {
                    chunks.add(current.toString().trim());
                    current.setLength(0);
                }

                // append sentence (with a space if needed)
                if (current.length() > 0) current.append(' ');
                current.append(sent);
            }

            if (current.length() > 0) {
                chunks.add(current.toString().trim());
            }
        }

        return chunks;
    }


    public List<String> addDocument(String docId, String text) {
        List<String> createdIds = new ArrayList<>();

        List<String> chunks = chunkText(text);

        for (String chunk : chunks) {
            Metadata meta = new Metadata();
            meta.put("docId", docId);

            TextSegment seg = new TextSegment(chunk, meta);
            String id = vectorDb.addSegment(seg);

            createdIds.add(id);
        }

        return createdIds;
    }


    public List<VectorDbManager.SearchResult> search(String query, int topK) {
        if (query == null || query.isEmpty()) {
            return new ArrayList<>();
        }

        Metadata meta = new Metadata();
        TextSegment qSeg = new TextSegment(query, meta);

        Embedding emb = vectorDb.getEmbeddingModel().embed(qSeg).content();
        return vectorDb.search(emb, topK, 0.6);
    }


    
}
