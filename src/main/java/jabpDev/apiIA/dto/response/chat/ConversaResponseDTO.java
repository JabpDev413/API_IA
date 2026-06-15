package jabpDev.apiIA.dto.response.chat;

import java.util.List;

public record ConversaResponseDTO(
        Long conversaId,
        Long usuaId,
        String titulo,
        String data,
        String hora,
        List<MessageResponseDTO> messagens
) {
}
