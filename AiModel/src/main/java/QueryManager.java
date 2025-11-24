import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class QueryManager {
    private RagService rag;
    private VectorDbManager db;
    private ModelService model;

    public QueryManager(String api_key, String model_name) {

        this.db = new VectorDbManager();
        this.model = new ModelService(api_key, model_name);
        this.rag = new RagService(
                db,
                model.getModel()
        );


    }

    public String answerQuery(String query) {
        List <String> relevantDocs = new ArrayList<>();
        List<VectorDbManager.SearchResult> results = rag.search(query, 3);
        for (VectorDbManager.SearchResult res : results) {
            relevantDocs.add(res.getText());
        }

        if (relevantDocs.isEmpty()) {
            return model.generateResponse(query);
        }

        String context = String.join("\n\n", relevantDocs);

        String augmentedPrompt = buildPromptWithContext(query, context);

        return model.generateResponse(augmentedPrompt);
    }

    private String buildPromptWithContext(String query, String context) {
        return String.format("""
                Use the following context to answer the question. 
                If the context doesn't contain relevant information, say so and provide your best answer.
                
                Question: %s
                
                
                Context:
                %s
                
                
                Answer:""", query, context);
    }

    public RagService getRag() {
        return rag;
    }
    public ModelService getModel() {
        return model;
    }
    public  VectorDbManager getDb() {
        return db;
    }

    // TODO - Parse Docs
    public void parseTxt(String filePath) throws IOException {
        String doc = new String(Files.readAllBytes(Paths.get(filePath)));
        rag.addDocument(filePath, doc);
    }
    public void parsePDF(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("File not found: " + filePath);
        }

        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String doc = stripper.getText(document);
            rag.addDocument(filePath, doc);
        }
    }


}
