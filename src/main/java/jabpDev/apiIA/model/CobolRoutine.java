package jabpDev.apiIA.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class CobolRoutine {

    private String       programa;
    private String       nomeRotina;
    private String       codigo;
    private List<String> calls = new ArrayList<>();
    private List<String> copybooks = new ArrayList<>();
    private String       tipoRotina;
}
