package jabpDev.apiIA.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jabpDev.apiIA.dto.response.AgenteResponseDto;
import jabpDev.apiIA.dto.response.ProgramaDependeciaDto;
import jabpDev.apiIA.dto.response.RoutineResponseDto;
import jabpDev.apiIA.functionsIA.AgenteTools;
import jabpDev.apiIA.model.CobolRoutine;
import jabpDev.apiIA.utils.UtilsServices;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class AgenteService {

    private final ChatClient chatClient;
    private final CobolDependenciaSerivce cobolDependenciaService;
    private final UtilsServices utilsServices;

    @Autowired
    private AgenteTools agenteTools;


    public AgenteService(ChatClient.Builder builder,
                         CobolDependenciaSerivce cobolDependenciaService,
                         AgenteTools agenteTools,
                         UtilsServices utilsServices
    ) {
        this.chatClient = builder.build();
        this.cobolDependenciaService = cobolDependenciaService;
        this.utilsServices = utilsServices;
    }


//    public String buscarNomePrograma(String pergunta){
//
//        return chatClient
//                .prompt()
//                .system("""
//                        Você é um Agente Analista especializado em triagem de problemas de sistemas legados.
//                        Sua missão é ler o relato do usuário e extrair o programa COBOL alvo e o problema de forma estruturada.
//
//                        REGRAS RÍGIDAS DE COMPORTAMENTO:
//                        1. Se o input for um log de erro, mensagem de sistema ou menção a um código, identifique o provável programa (ex: FAT2001, NFC3020, etc).
//                        2. Se for uma conversa informal, amigável ou dúvida geral sem relação direta com erro de programa:
//                           - Defina "nomePrograma" como ""
//                           - Crie um título amigável como "CHAT" ou "Conversa"
//                           - Escreva uma resposta direta, cortês e prestativa no campo "problema".
//
//                        RETORNE EXCLUSIVAMENTE UM OBJETO JSON VÁLIDO. NÃO COMENTE NADA FORA DO JSON.
//
//                        FORMATO OBRIGATÓRIO DA RESPOSTA:
//                        {
//                          "nomePrograma": "NOME_DO_PROGRAMA_OU_VAZIO",
//                          "titulo": "TITULO_CURTO_E_DIRETO_DA_DUVIDA_OU_CHAT",
//                          "problema": "RESUMO_DO_PROBLEMA_TECNICO_OU_A_RESPOSTA_AMIGAVEL_DA_CONVERSA"
//                        }
//
//                        EXEMPLOS DE RESPOSTA:
//
//                        Entrada: "Olá, bom dia! Como vai?"
//                        Resposta:
//                        {
//                          "nomePrograma": "",
//                          "titulo": "CHAT",
//                          "problema": "Olá! Bom dia. Como posso te ajudar com os sistemas legados hoje?"
//                        }
//
//                        Entrada: "Deu erro de estouro de limite no programa FAT2002 na hora de faturar"
//                        Resposta:
//                        {
//                          "nomePrograma": "FAT2002",
//                          "titulo": "Estouro de Limite",
//                          "problema": "Ocorreu uma falha por estouro de limite na rotina de faturamento"
//                        }
//
//                        Entrada: "Quais programas chamam o SES0000"
//                        Responsta:
//                        {
//                        programas: [
//                        ]
//                        }
//
//                        """)
//                .user("""
//                        ENTRADA DO USUÁRIO:
//                        --------------------
//                        %s
//                        --------------------
//                        """.formatted(pergunta))
////                .tools(agenteTools)
//                .call()
//                .content();
//
//    }

    //String programa, String problema, List<CobolRoutine> rotinas
    public AgenteResponseDto analizar(String problema){
        StringBuilder contexto = new StringBuilder();


//        for (CobolRoutine rotina : rotinas) {
//
//            contexto.append("""
//
//                    ROTINA:
//                    """);
//
//            contexto.append(rotina.getNomeRotina());
//
//            contexto.append("""
//
//
//                    CODIGO:
//                    """);
//
//            contexto.append(rotina.getCodigo());
//        }

        return chatClient
                .prompt()
                .system(utilsServices.getPromptSystem()
//                        """
//                        Você é um especialista em sistemas COBOL.
//
//                        Analise o problema informado.
//
//                        Explique:
//                        - possível causa
//                        - rotina relacionada
//                        - análise técnica
//                        - possíveis soluções
//
//                        Retorne APENAS JSON válido.
//
//                        EXEMPLOS DE RESPOSTA:
//
//                        Formato:
//                        {
//                          "descricaoErro": "",
//                          "causa": "",
//                          "solucao": "",
//                          "analiseTecnica": ""
//                        }
//
//                        Entrada: "Quais programas são chamados pelo SES0000"
//                        Responsta:
//                        {
//                        programas: [
//                        ]
//                        }
//                        """
                )
                .user("PROBLEMA OU ENTRADA DO USUÁRIO:\n" + problema)
                .tools(agenteTools)
                .call()
                .entity(AgenteResponseDto.class);

//        List<RoutineResponseDto> routines =
//                rotinas.stream()
//                        .map(rotina -> {
//                           return new RoutineResponseDto(
//                                   rotina.getPrograma(),
//                                   rotina.getNomeRotina(),
//                                   rotina.getCalls(),
//                                   rotina.getCopybooks(),
//                                   rotina.getTipoRotina()
//                           );
//                        }).toList();
//        List<ProgramaDependeciaDto> dependencias = cobolDependenciaService.gerarDependencias(rotinas);

//        return
//                new AgenteResponseDto(
//                "",
//                programa,
//                problema,
//                respostaIA,
//                rotinas.size(),
//                routines,
//                dependencias
//        );
    }

}
