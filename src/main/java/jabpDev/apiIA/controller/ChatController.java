package jabpDev.apiIA.controller;


import jabpDev.apiIA.dto.response.chat.ConversaResponseDTO;
import jabpDev.apiIA.dto.response.chat.MessageResponseDTO;
import jabpDev.apiIA.exception.ErrorException;
import jabpDev.apiIA.service.AgenteService;
import jabpDev.apiIA.service.chat.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversa")
@AllArgsConstructor
public class ChatController {

    private ChatService chatService;

    @PostMapping("/nova")
    public ConversaResponseDTO novaConversa() throws Exception{
        try {
            return chatService.novaConversa();
        } catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }

    @GetMapping("/buscar")
    public List<ConversaResponseDTO> buscarConversas()throws Exception{
        try {
            return chatService.buscarConversas();
        }catch (Exception e){
            throw new ErrorException("Conversas não encontradas");
        }
    }

    @GetMapping("/{conversaId}")
    public List<MessageResponseDTO> getConversa(@PathVariable Long conversaId) throws Exception {
        try {
            return chatService.buscarMensagensConversa(conversaId);
        } catch (Exception e) {
            throw new ErrorException("Conversa não encontrada");
        }
    }
}
