package com.main.AI.parser;

import com.main.AI.RagService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Parses plain-text based sources (.txt, .md, etc.).
 */
public final class TxtParseStrategy implements DocumentParseStrategy {


    public TxtParseStrategy() {
    }

    @Override
    public void parse(byte[] fileBytes, String documentId, RagService ragService) throws IOException {
        Objects.requireNonNull(fileBytes, "fileBytes cannot be null");
        Objects.requireNonNull(documentId, "documentId cannot be null");
        Objects.requireNonNull(ragService, "ragService cannot be null");

        String content = new String(fileBytes, StandardCharsets.UTF_8);
        ragService.addDocument(documentId, content);
    }
}


