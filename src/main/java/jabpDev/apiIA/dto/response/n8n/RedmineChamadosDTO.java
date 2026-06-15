package jabpDev.apiIA.dto.response.n8n;

import java.util.List;

public record RedmineChamadosDTO(
        List<RedmineSolicitacoesResponseDTO> chamados,
        Integer total_chamados
) {
}
