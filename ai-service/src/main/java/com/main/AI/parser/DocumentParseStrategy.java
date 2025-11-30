package com.main.AI.parser;

import com.main.AI.RagService;

import java.io.IOException;

public interface DocumentParseStrategy {

    void parse(byte[] fileBytes, String documentId, RagService ragService) throws IOException;
}


