package jabpDev.apiIA.dto.response.chat;

public record MessageResponseDTO(
        Long msgId,
        String msgTipo,
        String msgConteudo,
        String data,
        String hora
) {
}
