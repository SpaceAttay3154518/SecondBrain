import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import java.time.Duration;
import java.util.List;



public class ModelService {
    private final String defaultSystemPrompt = "You are a helpful assistant.";
    private final ChatLanguageModel chatModel;

    public ModelService(String apiKey, String modelName) {
        this.chatModel = OpenAiChatModel.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(0.8)
                .timeout(Duration.ofSeconds(60))
                .build();
    }



    public String generateResponse(String systemPrompt, String userMessage) {
        if (systemPrompt == null || systemPrompt.isBlank()) {
            systemPrompt = defaultSystemPrompt;
        }
        ChatRequest request = ChatRequest.builder()
                .messages(List.of(
                        new SystemMessage(systemPrompt),
                        new UserMessage(userMessage)
                ))
                .build();

        ChatResponse response = chatModel.chat(request);

        return response.aiMessage().text();
    }
    public String generateResponse(String userMessage) {
        return chatModel.generate(userMessage);
    }



    public String generateWithRag(String query) {
        /*List<String> relevantDocs = ragService.retrieveDocuments(query, 5);*/
        List<String> relevantDocs = List.of("Capital of Morocco has been changed to Berkan", "Breaking News : a New Capital of Morocco has been announced");
        if (relevantDocs.isEmpty()) {
            return generateResponse(query);
        }

        String context = String.join("\n\n", relevantDocs);

        String augmentedPrompt = buildPromptWithContext(query, context);

        return generateResponse(augmentedPrompt);
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

}
