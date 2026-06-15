package jabpDev.apiIA.dto.response.chat;

public record WebSocketResponseDTO(
        Long conversaId,
        Long usuarioId,
        String conteudo
) {
}
