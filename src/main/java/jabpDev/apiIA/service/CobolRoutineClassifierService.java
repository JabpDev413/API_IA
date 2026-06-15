package jabpDev.apiIA.service;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CobolRoutineClassifierService {

    public String classificar(String nomeRotina, String codigo) {
        String nome = nomeRotina.toUpperCase();
        String conteudo = codigo.toUpperCase();

        if (nome.contains("OPEN")
                || nome.contains("FILE")
                || conteudo.contains("OPEN INPUT")
                || conteudo.contains("READ ")) {

            return "ARQUIVO";
        }

        if (nome.contains("DISPLAY")
                || nome.contains("SCREEN")
                || conteudo.contains("DISPLAY WINDOW")
                || conteudo.contains("ACCEPT ")) {

            return "TELA";
        }

        if (nome.contains("VALID")
                || nome.contains("PROC")
                || conteudo.contains("ERROR")
                || conteudo.contains("MESSAGE")) {

            return "VALIDACAO";
        }

        if (conteudo.contains("CALL ")) {

            return "INTEGRACAO";
        }

        if (conteudo.contains("EXEC SQL")) {

            return "DATABASE";
        }

        return "NEGOCIO";
    }
}
