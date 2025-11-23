import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VectorDbManager {

    private EmbeddingStore<TextSegment> store;
    private final EmbeddingModel embeddingModel;

    public VectorDbManager() {
        this.store = new InMemoryEmbeddingStore<>();
        this.embeddingModel = new AllMiniLmL6V2EmbeddingModel();
    }

    public String addSegment(TextSegment segment) {
        Embedding embedding = embeddingModel.embed(segment).content();
        return store.add(embedding, segment);
    }

    public List<String> addSegments(List<TextSegment> segments) {
        List<String> ids = new ArrayList<>();
        for (TextSegment segment : segments) {
            String id = addSegment(segment);
            ids.add(id);
        }
        return ids;
    }


    public void deleteSegment(String id) {
        store.remove(id);
    }

    public String updateSegment(String id, TextSegment newSegment) {
        deleteSegment(id);
        return addSegment(newSegment);
    }

    public void clear() {
        store.removeAll();
    }

    public void saveToFile(String filePath) {
        ((InMemoryEmbeddingStore<TextSegment>) store).serializeToFile(filePath);
    }

    public void loadFromFile(String filePath) {
        store = InMemoryEmbeddingStore.fromFile(filePath);

    }


    public List<SearchResult> search(Embedding query, int maxResults, double minScore) {
        return store.findRelevant(query, maxResults, minScore).stream()
                .map(m -> new SearchResult(m.embeddingId(), m.embedded(), m.score()))
                .collect(Collectors.toList());
    }

    public EmbeddingModel getEmbeddingModel() {
        return embeddingModel;
    }

    public static class SearchResult {
        private final String id;
        private final TextSegment segment;
        private final double score;

        public SearchResult(String id, TextSegment segment, double score) {
            this.id = id;
            this.segment = segment;
            this.score = score;
        }

        public String getId() { return id; }
        public String getText() { return segment.text(); }
        public double getScore() { return score; }

        @Override
        public String toString() {
            return String.format("Score: %.4f | %s...",
                    score, getText().substring(0, Math.min(60, getText().length())));
        }
    }

}
