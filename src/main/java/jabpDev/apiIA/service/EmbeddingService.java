package jabpDev.apiIA.service;


import lombok.AllArgsConstructor;
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

    @Value("${HUGGINGFACE_API_KEY:}")
    private String hfApiKey;

    public List<Double> gerarEmbedding(String texto) {

        // 1. Usamos o modelo público e gratuito da HF (ótimo para português)
        Map<String, Object> request = Map.of(
                "inputs", texto
        );

        try {
            // 2. Fazemos o POST direto para os servidores da Hugging Face
            List response = restClient.post()
                    .uri("https://api-inference.huggingface.co/pipeline/feature-extraction/sentence-transformers/all-MiniLM-L6-v2")
                    .header("Authorization", "Bearer " + hfApiKey) // Envia o token hf_...
                    .body(request)
                    .retrieve()
                    .body(List.class);

            // A Hugging Face retorna direto uma lista de números (o vetor)
            return (List<Double>) response;

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
