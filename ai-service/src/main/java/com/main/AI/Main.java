package com.main.AI;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.data.document.Metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


// ================= TODO
//      - Better Chunking


public class Main {

    private static final String GROQ_API_KEY = Config.get("GROQ_API_KEY");
    private static final String MODEL_NAME = Config.get("MODEL_NAME");


    public static void queryManagerTest() throws IOException {

        // Initialize the QueryManager
        QueryManager qm = new QueryManager(GROQ_API_KEY, MODEL_NAME);


        // Test query
        String query = "Where can I find the Moroccan Traditional Food?";



        // Execute query
        String answer = qm.answerQuery(query);

        // Print result
        System.out.println("Query: " + query);
        System.out.println("Answer: " + answer);


        System.out.println("------------------------------------------");


        qm.parsePDF(Files.readAllBytes(Path.of("./LandOfFood.pdf")));
        /*qm.parseTxt("./LandOfFood.txt");*/
        // Test query
        query = "Where can I find the Moroccan Traditional Food?";



        // Execute query
        answer = qm.answerQuery(query);

        // Print result
        System.out.println("Query: " + query);
        System.out.println("Answer: " + answer);


    }

    public static void llmTest() {
        System.out.println("=== LLM Test ===\n");

        if (GROQ_API_KEY == null || GROQ_API_KEY.isEmpty()) {
            System.err.println("ERROR: GROQ_API_KEY environment variable not set!");
            return;
        }
        ModelService Llama = new ModelService(GROQ_API_KEY, MODEL_NAME);
        System.out.println(Llama.generateResponse("What is the capital of Morocco"));
        System.out.println("-------------------------------------------");
        System.out.println(Llama.generateResponse("You are not a helpful AI Assistant","What is the capital of Morocco?"));
    }

    public static void ragTest () {

        VectorDbManager vectorDb = new VectorDbManager();

        ModelService model = new ModelService(GROQ_API_KEY, MODEL_NAME);

        // Start RAG
        RagService rag = new RagService(
                vectorDb,
                model.getModel()
        );

        // Test Document
        String text = """
                Boujem3a Food is one of the best Restaurant in the Magical Land of Makla, he was rated 4,99 stars 
                and has been nominated for the prize of the most visited restaurant, the restaurant won awards
                in numerous dishes including Couscous, Tajine and Briwat m3amrin.\n\n However, his rival UM6P
                Resto has been more than capable of putting up a fight, in fact, the latter managed to win also prizes
                in dishes like Pizza and Guillotine de poulet, needless to say, the Magical Land Of Makla continues
                to experience top tier food due to this rivalry and might surpass what people expect.\n\n
                But within the deep caves of the Magical Land of Makla, laid dormant a new competitor. a beast that
                might portray itself as danger, since it did 99 years ago, where it used a forbidden spell to cook the   
                best L7em with ber9o9 on Land, But it spread a disease that forced people to sleep for ages. The Legends
                says that the seal wont hold for so long, and might be at any moment, pulling back the Magic Land of Makla
                back into the abyss of darkness, the land has yet to experience the wrath of Hamid The Destroyer. 
                """;

        System.out.println("Adding document…");

        List<String> ids = rag.addDocument("doc1", text);

        // print added IDs
        for (String id : ids) {
            System.out.println("Added chunk id: " + id);
        }
        vectorDb.saveToFile("./src/main/resources/dbs/1234.db");

        String query = "I want to eat italian food";

        System.out.println("\nSearching…");

        List<VectorDbManager.SearchResult> results = rag.search(query, 3);
        for (VectorDbManager.SearchResult res : results) {
            System.out.println("Match: " + res.getText());
        }

        System.out.println("\nLLM Response:");
        String answer = rag.answer(query, 3);

        System.out.println(answer);
    }


    public static void vectorDbTest() {
        VectorDbManager vectorDb = new VectorDbManager();

        Metadata meta = new Metadata();

        // Add single segment
        TextSegment segment1 = new TextSegment("Taza Snacks has got the best Couscous", meta);
        String id1 = vectorDb.addSegment(segment1);

        // Add multiple segments
        TextSegment segment2 = new TextSegment("UM6P Restaurant cooks the best Pizza", meta);
        TextSegment segment3 = new TextSegment("Boujem3a Food has the best Burger", meta);
        List<String> ids = vectorDb.addSegments(Arrays.asList(segment2, segment3));

        System.out.println("Added segment IDs: " + id1 + ", " + ids);
        System.out.println("Added multiple segment IDs: " + ids);

        // Search for similar segments using one of the embeddings
        Embedding queryEmbedding = vectorDb.getEmbeddingModel().embed(
                new TextSegment("where can I find good Pizza or Couscous?", meta)
        ).content();

        List<VectorDbManager.SearchResult> results = vectorDb.search(queryEmbedding, 5, 0.0);
        System.out.println("\nSearch results:");
        for (VectorDbManager.SearchResult r : results) {
            System.out.println(r);
        }

        // Update a segment
        String updatedId = vectorDb.updateSegment(id1, new TextSegment("No, actually my mum's food is the best", meta));
        System.out.println("\nUpdated segment ID: " + updatedId);

        // Delete a segment
        vectorDb.deleteSegment(ids.get(0));
        System.out.println("Deleted segment ID: " + ids.get(1));

        // Print remaining segments
        System.out.println("\nRemaining segments in vector DB:");
        for (VectorDbManager.SearchResult r : vectorDb.search(queryEmbedding, 10, 0.0)) {
            System.out.println(r);
        }

        vectorDb.saveToFile("./vector.db");

        // Restarting
        vectorDb = new VectorDbManager();
        vectorDb.loadFromFile("./vector.db");
        System.out.println("\nLoqded segments in vector DB:");
        for (VectorDbManager.SearchResult r : vectorDb.search(queryEmbedding, 10, 0.0)) {
            System.out.println(r);
        }


    }

    private static void addFile(Scanner scanner, QueryManager qm) throws IOException {
        System.out.print("Enter the full path of the file: ");
        String path = scanner.nextLine();
        File file = new File(path);
        String fileName = file.getName().toLowerCase();

        Path filepath = Path.of(path);
        if (fileName.endsWith(".txt") || fileName.endsWith(".md")) {
            qm.parseTxt(Files.readAllBytes(filepath));
        } else if (fileName.endsWith(".pdf")) {
            qm.parsePDF(Files.readAllBytes(filepath));
        } else {
            System.out.println("Unsupported file type. Only .txt, .md, or .pdf are allowed.");
        }


    }

    private static void askAI(Scanner scanner, QueryManager qm) {
        System.out.print("Ask the AI a question: ");
        String query = scanner.nextLine();


        // AI response
        String answer = qm.answerQuery(query);

        System.out.println("Query: " + query);
        System.out.println("Answer: " + answer);
    }


    public static void fullTest() throws IOException {

        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        QueryManager qm = new QueryManager(GROQ_API_KEY, MODEL_NAME);

        while (running) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Add a file");
            System.out.println("2. Ask the AI something");
            System.out.println("3. Clear DB");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addFile(scanner, qm);
                    break;
                case "2":
                    askAI(scanner, qm);
                    break;
                case "3":
                    qm.getDb().clear();
                    break;
                case "4":
                    running = false;
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }

        scanner.close();

    }

    public static void main(String[] args) throws IOException {
        ragTest();
    }

}