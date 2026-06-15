package jabpDev.apiIA.service;


import jabpDev.apiIA.model.CobolRoutine;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class CobolParseService {

    private CobolRoutineClassifierService classifierService;

    public List<CobolRoutine> processar(String conteudo) {

        List<CobolRoutine> rotinas = new ArrayList<>();

        String programa = extrairProgramId(conteudo);

        Pattern rotinaPattern =
                Pattern.compile(
                        "(?m)^\\s{0,7}(\\d{4}-[A-Z0-9-]+)\\."
                );

        Matcher matcher = rotinaPattern.matcher(conteudo);

        List<Integer> posicoes = new ArrayList<>();
        List<String> nomes = new ArrayList<>();

        while (matcher.find()) {

            posicoes.add(matcher.start());

            nomes.add(matcher.group(1));
        }

        for (int i = 0; i < posicoes.size(); i++) {

            int inicio = posicoes.get(i);

            int fim =
                    (i + 1 < posicoes.size())
                            ? posicoes.get(i + 1)
                            : conteudo.length();

            String bloco =
                    conteudo.substring(inicio, fim);

            CobolRoutine rotina = new CobolRoutine();

            rotina.setPrograma(programa);

            rotina.setNomeRotina(nomes.get(i));

            rotina.setCodigo(bloco);

            rotina.setCalls(extrairCalls(bloco));

            rotina.setCopybooks(extrairCopy(bloco));

            rotina.setTipoRotina(
                    classifierService.classificar(
                            rotina.getNomeRotina(),
                            bloco
                    )
            );

            rotinas.add(rotina);
        }

        return rotinas;
    }

    private String extrairProgramId(String conteudo) {

        Pattern pattern =
                Pattern.compile(
                        "PROGRAM-ID\\.\\s+([A-Z0-9]+)",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher matcher = pattern.matcher(conteudo);

        if (matcher.find()) {

            return matcher.group(1);
        }

        return "DESCONHECIDO";
    }

    private List<String> extrairCalls(String bloco) {

        List<String> calls = new ArrayList<>();

        Pattern pattern =
                Pattern.compile(
                        "CALL\\s+\"([A-Z0-9]+)\"",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher matcher = pattern.matcher(bloco);

        while (matcher.find()) {

            calls.add(matcher.group(1));
        }

        return calls;
    }

    private List<String> extrairCopy(String bloco) {

        List<String> copy = new ArrayList<>();

        Pattern pattern =
                Pattern.compile(
                        "COPY\\s+\"?([A-Z0-9.-]+)\"?",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher matcher = pattern.matcher(bloco);

        while (matcher.find()) {

            copy.add(matcher.group(1));
        }

        return copy;
    }
}
