package jabpDev.apiIA.service;

import jabpDev.apiIA.model.CobolRoutine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import java.util.List;

@Service
public class CobolFileLocatorService {

    @Value("${cobol.fontes.path}")
    private String fontesPath;


    public Optional<Path> localizarPrograma(String programa){
        try (Stream<Path> paths =
                     Files.walk(Paths.get(fontesPath))) {

            return paths
                    .filter(Files::isRegularFile)
                    .filter(path ->
                            path.getFileName()
                                    .toString()
                                    .equalsIgnoreCase(programa + ".cbl"))
                    .findFirst();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<CobolRoutine> buscarRotinasRelevantes(List<CobolRoutine> rotinas, String problema){
        List<CobolRoutine> relevantes = new ArrayList<>();

        String problemaLower = problema.toLowerCase();

        for(CobolRoutine rotina: rotinas){
            String codigo = rotina.getCodigo().toLowerCase();
            if (codigo.contains("lote")
                    || codigo.contains("error")
                    || codigo.contains("open")
                    || codigo.contains("read")
                    || codigo.contains("file")
                    || codigo.contains("ca-message")
                    || codigo.contains("ca-hint")
                    || codigo.contains("camsgbox")
            ) {

                relevantes.add(rotina);
            }
        }
        return relevantes;
    }
}
