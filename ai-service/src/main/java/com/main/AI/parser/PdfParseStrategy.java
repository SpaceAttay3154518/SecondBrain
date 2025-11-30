package com.main.AI.parser;

import com.main.AI.RagService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.util.Objects;

/**
 * Parses PDF documents by extracting their textual content.
 */
public final class PdfParseStrategy implements DocumentParseStrategy {

    public PdfParseStrategy() {
    }

    @Override
    public void parse(byte[] fileBytes, String documentId, RagService ragService) throws IOException {
        Objects.requireNonNull(fileBytes, "fileBytes cannot be null");
        Objects.requireNonNull(documentId, "documentId cannot be null");
        Objects.requireNonNull(ragService, "ragService cannot be null");

        try (PDDocument document = PDDocument.load(fileBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String extractedText = stripper.getText(document);
            ragService.addDocument(documentId, extractedText);
        }
    }
}


