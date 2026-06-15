package jabpDev.apiIA.dto.request.chat;

public record WebSocketRequestDTO(
        Long conversaId,
        String tipo,
        String conteudo
) {
}
