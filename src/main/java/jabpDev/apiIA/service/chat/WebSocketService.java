package jabpDev.apiIA.service.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import jabpDev.apiIA.dto.response.chat.ConversaResponseDTO;
import jabpDev.apiIA.dto.response.chat.MessageResponseDTO;
import jabpDev.apiIA.dto.response.chat.WebSocketResponseDTO;
import jabpDev.apiIA.entity.Usuario;
import jabpDev.apiIA.exception.ErrorException;
import jabpDev.apiIA.utils.UtilsServices;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class WebSocketService {


    private static ConcurrentHashMap<Long, WebSocketSession> usuariosList = new ConcurrentHashMap<>();
    private ChatService chatService;
    private UtilsServices utilsServices;


    public void regitrarSession(Long usuarioId, WebSocketSession socketSession){
        usuariosList.put(usuarioId, socketSession);
    }

    public void removeSession(Long usuarioId){
        usuariosList.remove(usuarioId);
    }

    public void sendMessageIA(WebSocketResponseDTO socketResponseDTO){
        try {
            ConversaResponseDTO conversaResponseDTO = chatService.conversarIa(socketResponseDTO);
            WebSocketSession webSocketSession = usuariosList.get(socketResponseDTO.usuarioId());
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(conversaResponseDTO);
            webSocketSession.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            System.err.println("sendMessageIA 1: " + e.getMessage());
            throw new ErrorException(e.getMessage());
        } catch (Exception e) {
             System.err.println("sendMessageIA 2: " + e.getMessage());
            throw new ErrorException(e.getMessage());
        }
    }
}
