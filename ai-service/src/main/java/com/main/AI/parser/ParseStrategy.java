package com.main.AI.parser;

import com.main.AI.RagService;

import java.io.IOException;
import java.util.Objects;

/**
 * Context class for the strategy pattern. Holds the current strategy and delegates parsing.
 */
public final class ParseStrategy {

    private DocumentParseStrategy strategy;

    public void setStrategy(DocumentParseStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy cannot be null");
    }

    public void parse(byte[] fileBytes, String documentId, RagService ragService) throws IOException {
        if (strategy == null) {
            throw new IllegalStateException("Parsing strategy not set");
        }
        strategy.parse(fileBytes, documentId, ragService);
    }
}


