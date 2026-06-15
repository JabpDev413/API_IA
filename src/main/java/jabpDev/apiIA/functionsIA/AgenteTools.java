package jabpDev.apiIA.functionsIA;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jabpDev.apiIA.dto.response.AgenteResponseDto;
import jabpDev.apiIA.dto.response.ProgramaDependeciaDto;
import jabpDev.apiIA.dto.response.RoutineResponseDto;
import jabpDev.apiIA.entity.CobolRoutineEntity;
import jabpDev.apiIA.entity.HSChamadas;
import jabpDev.apiIA.entity.HSMensagens;
import jabpDev.apiIA.model.CobolRoutine;
import jabpDev.apiIA.repository.CobolRoutineRepository;
import jabpDev.apiIA.repository.HSChamadasRepository;
import jabpDev.apiIA.repository.HSMensagensRepository;
import jabpDev.apiIA.service.CobolDependenciaSerivce;
import jabpDev.apiIA.service.CobolFileLocatorService;
import jabpDev.apiIA.service.CobolParseService;
import jabpDev.apiIA.utils.UtilsServices;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
public class AgenteTools {
    private final HSChamadasRepository chamadasRepository;
    private final HSMensagensRepository hsMensagensRepository;
    private final CobolFileLocatorService cobolFileLocatorService;
    private final UtilsServices utilsServices;
    private final CobolParseService parserService;
    private final CobolDependenciaSerivce cobolDependenciaService;

    public AgenteTools(HSChamadasRepository chamadasRepository,
                       HSMensagensRepository hsMensagensRepository,
                       CobolFileLocatorService cobolFileLocatorService,
                       UtilsServices utilsServices,
                       CobolParseService parserService,
                       CobolDependenciaSerivce cobolDependenciaService
    ) {
        this.chamadasRepository = chamadasRepository;
        this.hsMensagensRepository = hsMensagensRepository;
        this.cobolFileLocatorService = cobolFileLocatorService;
        this.utilsServices = utilsServices;
        this.parserService = parserService;
        this.cobolDependenciaService = cobolDependenciaService;
    }

    // buscarProgramasChamados
    // Programa chamador que chega da IA
    public record ProgramaChegaIARequest(String chamador){}
    // Programas chamados
    public record ProgramasChamadosResponse(List<String> chamados){}

    //buscarMensagens
    public record MensagensChegaIARequest(String texto){}
    public record MensagensIAResponse(List<String> programas){}

    //analizar
    public record AnalizarIAResponse(String programa, String context, List<CobolRoutine> relevantes, List<CobolRoutine> rotinas){}


    @Tool(name = "buscarProgramasChamados",description = "Busca no banco os programas que sao chamados pelo programa que chega da IA")
    public ProgramasChamadosResponse buscarProgramasChamados(ProgramaChegaIARequest chamador) {

        if (chamador.chamador().isEmpty()) {
            return new ProgramasChamadosResponse(List.of());
        }

        List<String> hsChamadasList = chamadasRepository.findByChamador(chamador.chamador());
        if (hsChamadasList.isEmpty()){
            return new ProgramasChamadosResponse(List.of());
        }

        return new ProgramasChamadosResponse(
                new ArrayList<>(hsChamadasList)
        );
    }

//    @Tool(name = "buscarLinhaDeMensagem", description = "Busca no banco a linha onde aparece a mensagem de erro informada")
//    public MensagensIAResponse buscarLinhaDeMensagem(MensagensChegaIARequest request){
//        if (request.texto().isEmpty()){
//            return new MensagensIAResponse(List.of());
//        }
//
//        List<String> responseBD = hsMensagensRepository.buscaProgramas(request.texto());
//        if (responseBD == null || responseBD.isEmpty()){
//            return new MensagensIAResponse(List.of());
//        }
//        return new MensagensIAResponse(responseBD);
//    }

    @Tool(name = "analizarContextoERotinasDeUmPrograma", description = "Busca o fonte de um programa especifico, parsea e devolve um conceito dependendo do contexto passado pelo usuario")
    public AnalizarIAResponse analizarContextoERotinasDeUmPrograma(String programa,String context){


        List<CobolRoutine> rotinas = List.of();
        Optional<Path> arquivo;

        System.err.println("Programa: " + programa);

        if (!programa.isEmpty()){
            arquivo =
                    cobolFileLocatorService.localizarPrograma(
                            programa
                    );
            try {
                System.out.println("Arquivo: " + arquivo.get());

                String conteudo = utilsServices.lerArquivo(arquivo.get());

                System.out.println("Arquivo lido com sucesso");

                rotinas =
                        parserService.processar(conteudo);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Erro ao ler o arquivo: " + e.getMessage() + " - " + "programa: "+ programa);
                throw new RuntimeException(e.getMessage());
            }
        }

        List<CobolRoutine> relevantes =
                cobolFileLocatorService
                        .buscarRotinasRelevantes(
                                rotinas,
                                context
                        );

        List<RoutineResponseDto> routines =
                rotinas.stream()
                        .map(rotina -> {
                            return new RoutineResponseDto(
                                    rotina.getPrograma(),
                                    rotina.getNomeRotina(),
                                    rotina.getCalls(),
                                    rotina.getCopybooks(),
                                    rotina.getTipoRotina()
                            );
                        }).toList();
        List<ProgramaDependeciaDto> dependencias = cobolDependenciaService.gerarDependencias(rotinas);
        System.err.println("parametros enviados: " + " programa: " +programa + " context: " + context + " relevantes: " + relevantes + " retinas: " + rotinas);

        return new AnalizarIAResponse(
                programa,
                context,
                relevantes,
                rotinas
        );
    }
}
