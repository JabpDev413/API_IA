package jabpDev.apiIA.controller;

import jabpDev.apiIA.dto.response.n8n.RedmineChamadosDTO;
import jabpDev.apiIA.dto.response.n8n.RedmineSolicitacoesResponseDTO;
import jabpDev.apiIA.service.n8n.N8nService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@AllArgsConstructor
@RequestMapping("/n8n")
public class N8nController {

    private N8nService n8nService;

    @GetMapping("/redmine/solicitacoes")
    public RedmineChamadosDTO buscarSolicitacoesRedmine() throws Exception {
        return n8nService.buscarSolicitacoesRedmine();
    }
}
