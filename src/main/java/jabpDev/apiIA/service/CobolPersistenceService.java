package jabpDev.apiIA.service;

import com.pgvector.PGvector;
import jabpDev.apiIA.dto.response.AnaliseIaDto;
import jabpDev.apiIA.dto.response.RoutineResponseDto;
import jabpDev.apiIA.entity.CobolRoutineEntity;
import jabpDev.apiIA.model.CobolRoutine;
import jabpDev.apiIA.repository.CobolRoutineRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CobolPersistenceService {

    private CobolRoutineRepository repository;
    private CobolRoutineClassifierService classifierService;
    private final EmbeddingService embeddingService;

//    public void salvarRotinas(
//            List<CobolRoutine> rotinas,
//            String descricaoErro,
//            String causa,
//            String solucao
//    ) {
//
//        for (CobolRoutine rotina : rotinas) {
//
//            String tipo = classifierService.classificar(
//                    rotina.getNomeRotina(),
//                    rotina.getCodigo()
//            );
//
//            // =====================================================
//            // CONTEXTO COMPLETO PARA O EMBEDDING
//            // =====================================================
//
//            String contexto = """
//                    Erro: %s
//
//                    Causa: %s
//
//                    Solução: %s
//
//                    Código COBOL:
//                    %s
//                    """.formatted(
//                    descricaoErro,
//                    causa,
//                    solucao,
//                    rotina.getCodigo()
//            );
//
//            // =====================================================
//            // GERA EMBEDDING
//            // =====================================================
//
//            List<Double> embedding =
//                    embeddingService.gerarEmbedding(contexto);
//
//            float[] embeddingArray =
//                    new float[embedding.size()];
//
//            for (int i = 0; i < embedding.size(); i++) {
//
//                embeddingArray[i] =
//                        embedding.get(i).floatValue();
//            }
//
//            // =====================================================
//            // MONTA ENTITY
//            // =====================================================
//
//            CobolRoutineEntity entity =
//                    new CobolRoutineEntity();
//
//            entity.setPrograma(
//                    rotina.getPrograma()
//            );
//
//            entity.setNomeRotina(
//                    rotina.getNomeRotina()
//            );
//
//            entity.setCodigo(
//                    rotina.getCodigo()
//            );
//
//            entity.setTipoRotina(
//                    tipo
//            );
//
//            entity.setDescricaoErro(
//                    descricaoErro
//            );
//
//            entity.setCausa(
//                    causa
//            );
//
//            entity.setSolucao(
//                    solucao
//            );
//
//            entity.setEmbedding(
//                    embeddingArray            );
//
//            entity.setCalls(rotina.getCalls());
//            entity.setCopys(rotina.getCopybooks());
//
//            repository.save(entity);
//        }
//    }

    public void salvarRotinas(
            List<CobolRoutine> rotinas, // Alterado para o DTO que a IA retorna
            String descricaoErro,
            String causa,
            String solucao,
            AnaliseIaDto analise,             // 🌟 Passamos o objeto completo da análise
            String respostaProUsuario         // 🌟 Passamos a resposta que foi pro chat
    ) {

        // =====================================================
        // CENÁRIO 1: NÃO HÁ ROTINAS (Dúvida geral, Chat ou Mapeamento)
        // =====================================================
        if (rotinas == null || rotinas.isEmpty()) {

            // Criamos um contexto textual rico baseado na pergunta e resposta do chat
            String contextoGlobal = """
                Contexto: Consulta / Dúvida Geral de Arquitetura COBOL
                Programa Alvo: %s
                Título: %s
                Pergunta/Problema: %s
                Análise Técnica: %s
                Resposta enviada ao Usuário: %s
                """.formatted(
                    analise.nomePrograma(),
                    analise.titulo(),
                    analise.problema(),
                    analise.analiseTecnica(),
                    respostaProUsuario
            );

            // Gera o embedding do bloco de texto da conversa
            List<Double> embedding = embeddingService.gerarEmbedding(contextoGlobal);
            float[] embeddingArray = new float[embedding.size()];
            for (int i = 0; i < embedding.size(); i++) {
                embeddingArray[i] = embedding.get(i).floatValue();
            }

            // Salva como uma entidade de conhecimento global no banco
            CobolRoutineEntity entity = new CobolRoutineEntity();
            entity.setPrograma(analise.nomePrograma() != null ? analise.nomePrograma() : "GLOBAL");
            entity.setNomeRotina("CONVERSA_OU_CONSULTA");
            entity.setCodigo(contextoGlobal); // Guardamos o histórico estruturado no campo de código
            entity.setTipoRotina("CHAT_CONSULTA");
            entity.setDescricaoErro(analise.descricaoErro());
            entity.setCausa(analise.causa());
            entity.setSolucao(analise.solucao());
            entity.setEmbedding(embeddingArray);
            entity.setCalls(new ArrayList<>());
            entity.setCopys(new ArrayList<>());

            repository.save(entity);
            System.out.println("Aprendizado de consulta salvo com sucesso!");
            return; // Finaliza o método aqui, pois não há sub-rotinas para iterar
        }

        // =====================================================
        // CENÁRIO 2: EXISTEM ROTINAS (Seu fluxo original de erro/código)
        // =====================================================
        for (CobolRoutine rotina : rotinas) {

            String tipo = classifierService.classificar(
                    rotina.getNomeRotina(),
                    rotina.getCodigo()
            );

            String contexto = """
                Erro: %s
                
                Causa: %s
                
                Solução: %s
                
                Código COBOL:
                %s
                """.formatted(
                    descricaoErro,
                    causa,
                    solucao,
                    rotina.getCodigo()
            );

            List<Double> embedding = embeddingService.gerarEmbedding(contexto);
            float[] embeddingArray = new float[embedding.size()];
            for (int i = 0; i < embedding.size(); i++) {
                embeddingArray[i] = embedding.get(i).floatValue();
            }

            CobolRoutineEntity entity = new CobolRoutineEntity();
            entity.setPrograma(rotina.getPrograma());
            entity.setNomeRotina(rotina.getNomeRotina());
            entity.setCodigo(rotina.getCodigo());
            entity.setTipoRotina(tipo);
            entity.setDescricaoErro(descricaoErro);
            entity.setCausa(causa);
            entity.setSolucao(solucao);
            entity.setEmbedding(embeddingArray);

            entity.setCalls(rotina.getCalls());
            entity.setCopys(rotina.getCopybooks());

            repository.save(entity);
        }
    }

    private float[] toArray(List<Float> embedding) {
        float[] arr = new float[embedding.size()];

        for (int i = 0; i < embedding.size(); i++) {
            arr[i] = embedding.get(i);
        }

        return arr;
    }
}
