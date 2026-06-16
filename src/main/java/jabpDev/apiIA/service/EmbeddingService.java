package jabpDev.apiIA.service;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService {

    private final RestClient restClient = RestClient.create();

    @Value("${cohere.api.key}")
    private String cohereApiKey;

    public List<Double> gerarEmbedding(String texto) {

        // Estrutura oficial da API da Cohere V1
        Map<String, Object> request = Map.of(
                "texts", List.of(texto), // Ela aceita uma lista de textos
                "model", "embed-multilingual-v3.0", // Modelo perfeito para Português
                "input_type", "search_query" // Avisa a IA que isso é para busca semântica
        );

        try {
            // Rota oficial global da Cohere (super estável no Render)
            Map response = restClient.post()
                    .uri("https://api.cohere.com/v1/embed")
                    .header("Authorization", "Bearer " + cohereApiKey.trim())
                    .header("Content-Type", "application/json")
                    .body(request)
                    .retrieve()
                    .body(Map.class);

            // A Cohere retorna uma estrutura contendo uma lista dentro de outra: "embeddings": [[0.1, 0.2, ...]]
            List<List<Object>> embeddingsList = (List<List<Object>>) response.get("embeddings");

            // Pegamos a primeira lista de números e convertemos para Double
            return embeddingsList.get(0).stream()
                    .map(num -> ((Number) num).doubleValue())
                    .toList();

        } catch (Exception e) {
            System.err.println("Erro ao chamar API de Embeddings da Hugging Face: " + e.getMessage());
            throw e;
        }
    }

//    public List<Double> gerarEmbedding(String texto) {
//
//        Map<String, Object> request = Map.of(
//                "model", "nomic-embed-text",
//                "prompt", texto
//        );
//
//        Map response = restClient.post()
//                .uri("http://localhost:11434/api/embeddings")
//                .body(request)
//                .retrieve()
//                .body(Map.class);
//
//        return (List<Double>) response.get("embedding");
//    }
}
