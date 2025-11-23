import java.util.List;
import java.util.Scanner;


// ================= TODO
//      - Rag
//      - VectorDB


public class Main {

    private static final String GROQ_API_KEY = Config.get("GROQ_API_KEY");
    private static final String MODEL_NAME = Config.get("MODEL_NAME");

    public static void main(String[] args) {
        System.out.println("=== Main Test ===\n");

        // Check API key
        if (GROQ_API_KEY == null || GROQ_API_KEY.isEmpty()) {
            System.err.println("ERROR: GROQ_API_KEY environment variable not set!");
            return;
        }
        ModelService Llama = new ModelService(GROQ_API_KEY, MODEL_NAME);
        System.out.println(Llama.generateResponse("What is the capital of Morocco"));
        System.out.println("-------------------------------------------");
        System.out.println(Llama.generateResponse("You are not a helpful AI Assistant","What is the capital of Morocco?"));
        System.out.println("-------------------------------------------");
        System.out.println(Llama.generateWithRag("What is the capital of Morocco?"));
    }

}