import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
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
    public ChatLanguageModel getModel() {
        return chatModel;
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




}
