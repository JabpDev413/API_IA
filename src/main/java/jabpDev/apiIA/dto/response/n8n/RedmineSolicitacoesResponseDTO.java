package jabpDev.apiIA.dto.response.n8n;

public record RedmineSolicitacoesResponseDTO(
        Long id,
        String solicitante,
        String status,
        Integer total_dias,
        String data
) {
}