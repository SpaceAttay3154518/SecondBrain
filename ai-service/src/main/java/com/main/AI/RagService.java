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
        List<String> chunks = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return chunks;
        }

        // Split on one or more newlines (\n or \r\n)
        String[] paragraphs = text.split("\n\\R+");

        for (String p : paragraphs) {
            String cleaned = p.trim();
            if (!cleaned.isEmpty()) {
                chunks.add(cleaned);
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
        return vectorDb.search(emb, topK, 0.4);
    }


    public String answer(String query, int topK) {
        List<VectorDbManager.SearchResult> results = search(query, topK);

        StringBuilder ctx = new StringBuilder();
        int count = 1;

        for (VectorDbManager.SearchResult r : results) {
            ctx.append("[Context ").append(count++).append(" | score=")
                    .append(String.format("%.4f", r.getScore())).append("]\n")
                    .append(r.getText()).append("\n\n");
        }

        String prompt;
        if (results.isEmpty()) {
            prompt = query;
        } else {
            prompt = "Use the following context to answer the question.\n\n"
                    + ctx + "Question: " + query + "\nAnswer:";
        }

        ChatRequest request = ChatRequest.builder()
                .messages(List.of(
                        new SystemMessage(systemPrompt),
                        new UserMessage(prompt)
                ))
                .build();

        ChatResponse response = chatModel.chat(request);
        if (response == null || response.aiMessage() == null) {
            return null;
        }

        return response.aiMessage().text();
    }
}
