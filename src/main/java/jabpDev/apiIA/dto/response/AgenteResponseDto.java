package jabpDev.apiIA.dto.response;

import jabpDev.apiIA.model.CobolRoutine;

import java.util.List;

public record AgenteResponseDto(
        String titulo,
        String programa,
        String problema,
        String respostaIA,
        String respostaProUsuario,
        Integer totalRotinasAnalisadas,
        List<RoutineResponseDto> rotinas,
        List<ProgramaDependeciaDto> dependencias,
        AnaliseIaDto analiseIa,
        List<CobolRoutine> routinas
) {
}
