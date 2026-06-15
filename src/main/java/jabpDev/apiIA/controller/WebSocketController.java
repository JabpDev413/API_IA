package jabpDev.apiIA.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jabpDev.apiIA.dto.request.chat.WebSocketRequestDTO;
import jabpDev.apiIA.dto.response.chat.WebSocketResponseDTO;
import jabpDev.apiIA.entity.Usuario;
import jabpDev.apiIA.exception.ErrorException;
import jabpDev.apiIA.service.chat.WebSocketService;
import jabpDev.apiIA.utils.UtilsServices;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@MessageMapping("/messages")
public class WebSocketController extends TextWebSocketHandler {

    private UtilsServices utilsServices;
    private ObjectMapper objectMapper = new ObjectMapper();
    private WebSocketService webSocketService;


    @Override
    public void afterConnectionEstablished(WebSocketSession socketSession) throws IOException{
        try {
            Usuario usuario = utilsServices.getToken(socketSession);
            webSocketService.regitrarSession(usuario.getId(), socketSession);
            WebSocketResponseDTO socketResponseDTO = new WebSocketResponseDTO(
                    0L,
                    0L,
                    "Conectado"
            );
            String json = objectMapper.writeValueAsString(socketResponseDTO);
            socketSession.sendMessage(new TextMessage(json));
        } catch (Exception e) {
            System.err.println("connection: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession socketSession, TextMessage message) throws IOException{
        try {
            Usuario usuario = utilsServices.getToken(socketSession);
            WebSocketRequestDTO socketRequestDTO = objectMapper.readValue(message.getPayload(), WebSocketRequestDTO.class);
            WebSocketResponseDTO socketResponseDTO = new WebSocketResponseDTO(
                    socketRequestDTO.conversaId(),
                    usuario.getId(),
                    socketRequestDTO.conteudo()
            );
            webSocketService.sendMessageIA(socketResponseDTO);
        }catch (Exception e){
            System.err.println("Erro ao receber msg: " + e.getMessage());
             WebSocketResponseDTO exception = new WebSocketResponseDTO(
                    0L,
                    0L,
                    "Erro ao conversar com IA");
            String json = objectMapper.writeValueAsString(exception);
            socketSession.sendMessage(new TextMessage(json));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession socketSession, CloseStatus closeStatus) throws IOException{
        try {
            Usuario usuario = utilsServices.getToken(socketSession);
            webSocketService.removeSession(usuario.getId());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
