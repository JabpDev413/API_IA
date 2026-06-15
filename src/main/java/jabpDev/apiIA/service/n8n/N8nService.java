package jabpDev.apiIA.service.n8n;

import jabpDev.apiIA.dto.response.n8n.RedmineChamadosDTO;
import jabpDev.apiIA.dto.response.n8n.RedmineSolicitacoesResponseDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class N8nService {

    private final RestClient restClient;

    public N8nService(RestClient.Builder restClientBuilder) {
        // 1. Configura os limites de tempo (timeouts) para o RestClient não travar infinitamente
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) Duration.ofSeconds(5).toMillis()); // 5 segundos para conectar
        requestFactory.setReadTimeout((int) Duration.ofSeconds(45).toMillis());   // 45 segundos para esperar o n8n responder

        this.restClient = restClientBuilder
                .baseUrl("http://localhost:5678")
                .requestFactory(requestFactory) // Aplica a configuração de timeout
                .build();
    }

    @Async
    public RedmineChamadosDTO buscarSolicitacoesRedmine() {
        System.err.println("Buscando solicitações do Redmine de forma assíncrona...");

        try {
            // Agora mapeia para N8nResponseWrapperDTO em vez de List diretamente
            RedmineChamadosDTO wrapper = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/webhook/issues-cliente")
                            .queryParam("X-Redmine-API-Key", "02e94b53340376e7a54d7f4d6a6ae3b4baf0fa47")
                            .build())
                    .retrieve()
                    .body(RedmineChamadosDTO.class); // Mapeia a estrutura do objeto raiz

            if (wrapper != null && wrapper.chamados() != null) {
                System.err.println("Solicitações recebidas. Total: " + wrapper.total_chamados());
                return new RedmineChamadosDTO(wrapper.chamados(), wrapper.total_chamados()); // Retorna apenas a lista interna
            }

            return new RedmineChamadosDTO(List.of(), 0); // Retorna lista vazia se não houver dados

        } catch (Exception e) {
            System.err.println("Erro ao buscar solicitações do Redmine: " + e.getMessage());
            return new RedmineChamadosDTO(List.of(), 0); // Retorna lista vazia em caso de erro
        }
    }

}
