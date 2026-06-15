package jabpDev.apiIA.components;


import jabpDev.apiIA.entity.CobolRoutineEntity;
import jabpDev.apiIA.model.CobolRoutine;
import org.springframework.stereotype.Component;

@Component
public class CobolRoutineMapper {

    public CobolRoutineEntity toEntity(CobolRoutine rotina) {
        CobolRoutineEntity entity =
                new CobolRoutineEntity();

        entity.setPrograma(
                rotina.getPrograma()
        );

        entity.setNomeRotina(
                rotina.getNomeRotina()
        );

        entity.setTipoRotina(
                rotina.getTipoRotina()
        );

        entity.setCodigo(
                rotina.getCodigo()
        );

        return entity;
    }
}
