package jabpDev.apiIA.service;

import jabpDev.apiIA.entity.CobolRoutineEntity;
import jabpDev.apiIA.repository.CobolRoutineRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class SemanticaBuscarService {

    private CobolRoutineRepository cobolRoutineRepository;
    private EmbeddingService embeddingService;


    public List<CobolRoutineEntity> busquedaSemantica(String query) {

        List<Double> embedding =
                embeddingService.gerarEmbedding(query);

        float[] queryVector =
                toFloatArray(embedding);

        List<CobolRoutineEntity> rotinas =
                cobolRoutineRepository.findAll();

        return rotinas.stream()

                // calcula score
                .map(rotina -> {

                    float score =
                            cosine(
                                    queryVector,
                                    rotina.getEmbedding()
                            );

                    rotina.setSimilarityScore(score);

                    return rotina;
                })

                // filtro mínimo
                .filter(r -> r.getSimilarityScore() > 0.75f)

                // ordena
                .sorted(
                        Comparator.comparing(
                                CobolRoutineEntity::getSimilarityScore
                        ).reversed()
                )

                .limit(5)

                .toList();
    }

    private float[] toFloatArray(List<Double> list) {

        float[] array = new float[list.size()];

        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i).floatValue();
        }

        return array;
    }

    private float cosine(float[] a, float[] b) {

        if (a == null || b == null) {
            return 0f;
        }

        if (a.length != b.length) {
            return 0f;
        }

        float dot = 0f;
        float normA = 0f;
        float normB = 0f;

        for (int i = 0; i < a.length; i++) {

            dot += a[i] * b[i];

            normA += a[i] * a[i];

            normB += b[i] * b[i];
        }

        if (normA == 0 || normB == 0) {
            return 0f;
        }

        return (float)
                (dot / (
                        Math.sqrt(normA)
                                * Math.sqrt(normB)
                ));
    }
}
