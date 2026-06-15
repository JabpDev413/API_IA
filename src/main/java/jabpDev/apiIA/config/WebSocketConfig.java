package jabpDev.apiIA.config;

import jabpDev.apiIA.controller.WebSocketController;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@AllArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private WebSocketController webSocketController;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry){
        System.err.println("webs registrou");
        webSocketHandlerRegistry.addHandler(webSocketController,"/messages")
//                .setAllowedOrigins("*")
                .setAllowedOriginPatterns("*");
    }
}
