package jabpDev.apiIA.agente;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jabpDev.apiIA.dto.request.PerguntaRequest;
import jabpDev.apiIA.dto.response.*;
import jabpDev.apiIA.entity.CobolRoutineEntity;
import jabpDev.apiIA.model.CobolRoutine;
import jabpDev.apiIA.service.*;
import jabpDev.apiIA.utils.UtilsServices;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class Agente {

    private CobolFileLocatorService cobolFileLocatorService;
    private final CobolParseService parserService;
//    private final CobolContextService contextService;
    private final AgenteService agenteService;
    private final CobolPersistenceService persistenceService;
    private final SemanticaBuscarService semanticSearchService;
    private final UtilsServices utilsServices;


    public RespostaProUsuarioResponseDTO analizar(String context) throws Exception {

        // =========================================
        // 1. BUSCA SEMÂNTICA
        // =========================================

        List<CobolRoutineEntity> similares =
                semanticSearchService.busquedaSemantica(
                        context
                );

        List<CobolRoutineEntity> relevantesSemanticos =
                similares.stream()

                        .filter(r ->
                                r.getSimilarityScore() != 0.0f
                                        && r.getSimilarityScore() > 0.80f
                        )

                        .toList();

        similares.forEach(r ->

                System.err.println(
                        "Rotinas similares: " +
                        r.getNomeRotina()
                                + " -> "
                                + r.getSimilarityScore()
                )
        );

        List<RoutineResponseDto> similaresDto =
                relevantesSemanticos.stream()

                        .map(r ->
                                new RoutineResponseDto(
                                        r.getPrograma(),
                                        r.getNomeRotina(),
                                        List.of(),
                                        List.of(),
                                        r.getTipoRotina()
                                )
                        )

                        .toList();

        // =========================================
        // 2. SE ENCONTROU NO BANCO
        // =========================================

        if (!similaresDto.isEmpty()) {
            System.err.println("Resposta encontrada no banco (sem uso de IA)");

            CobolRoutineEntity melhor =
                    relevantesSemanticos.getFirst();

            return new RespostaProUsuarioResponseDTO(
                    "Com referencia ao contexto informado foi achada a solução " + melhor.getSolucao(),
                    "",
                    melhor.getPrograma()
            );
        }

        // =========================================
        // 3. FALLBACK IA
        // =========================================

        AgenteResponseDto responseIA = agenteService.analizar(context);


//        String response = respostaIA.respostaIA()
//                .replace("```json", "")
//                .replace("```", "")
//                .trim();


//        ObjectMapper mapper = new ObjectMapper();
//        AnaliseIaDto analise = mapper.readValue(
//                json,
//                AnaliseIaDto.class
//        );

        AnaliseIaDto analise = new AnaliseIaDto(
                responseIA.analiseIa().descricaoErro(),
                responseIA.analiseIa().causa(),
                responseIA.analiseIa().solucao(),
                responseIA.analiseIa().analiseTecnica(),
                responseIA.analiseIa().nomePrograma(),
                responseIA.analiseIa().titulo(),
                responseIA.analiseIa().problema()
        );

        System.err.println("Analise: " + analise);

        // =========================================
        // 4. SALVA APRENDIZADO
        // =========================================

        persistenceService.salvarRotinas(

                responseIA.routinas(),

                analise.descricaoErro(),

                analise.causa(),

                analise.solucao(),
                analise,
                responseIA.respostaProUsuario()
        );

        // =========================================
        // 5. RETORNO
        // =========================================

        StringBuilder descricao = new StringBuilder();

        if (analise.causa() != null && !analise.causa().isBlank()) {
            descricao.append("""
        Possível causa:
        
        %s
        .
        """.formatted(analise.causa()));
        }

        if (analise.solucao() != null && !analise.solucao().isBlank()) {
            descricao.append("""
        Solução sugerida:
        
        %s
        .
        """.formatted(analise.solucao()));
        }

        return new RespostaProUsuarioResponseDTO(
                        responseIA.respostaProUsuario(),
                responseIA.titulo(),
                responseIA.programa()
        );
    }


}
