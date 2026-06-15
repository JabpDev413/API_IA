package jabpDev.apiIA.service;


import lombok.AllArgsConstructor;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService {

    private final RestClient restClient = RestClient.create();

    public List<Double> gerarEmbedding(String texto) {

        Map<String, Object> request = Map.of(
                "model", "nomic-embed-text",
                "prompt", texto
        );

        Map response = restClient.post()
                .uri("http://localhost:11434/api/embeddings")
                .body(request)
                .retrieve()
                .body(Map.class);

        return (List<Double>) response.get("embedding");
    }
}
