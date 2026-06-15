package jabpDev.apiIA.service;

import jabpDev.apiIA.dto.response.ProgramaDependeciaDto;
import jabpDev.apiIA.model.CobolRoutine;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CobolDependenciaSerivce {

    public List<ProgramaDependeciaDto> gerarDependencias(List<CobolRoutine> rotinas) {
        List<ProgramaDependeciaDto> dependencias =
                new ArrayList<>();


        for(CobolRoutine rotina : rotinas){
            for(String call : rotina.getCalls()){
                dependencias.add(
                        new ProgramaDependeciaDto(
                                rotina.getPrograma(),
                                call,
                                "CALL"

                        )
                );
            }
            for(String copy : rotina.getCopybooks()){
                dependencias.add(
                        new ProgramaDependeciaDto(
                                rotina.getPrograma(),
                                copy,
                                "COPY"

                        )
                );
            }
        }
        return dependencias;
    }
}
