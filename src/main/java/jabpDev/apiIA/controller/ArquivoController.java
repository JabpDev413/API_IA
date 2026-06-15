package jabpDev.apiIA.controller;

import jabpDev.apiIA.service.AgenteService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/arquivo")
public class ArquivoController {

    private AgenteService agenteService;

//    @PostMapping
//    public String uploadArquivo(@RequestParam MultipartFile arquivo, @RequestParam String pergunta) throws Exception {
//        String conteudo = new String(arquivo.getBytes());
//        return agenteService.buscarNomePrograma(conteudo);
//    }
}
