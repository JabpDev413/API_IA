package jabpDev.apiIA.dto.response;

import java.util.List;

public record RoutineResponseDto(
        String programa,
        String rotina,
        List<String> calls,
        List<String> copyBooks,
        String tipoRotina
) {
}
