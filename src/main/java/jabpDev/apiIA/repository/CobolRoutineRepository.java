package jabpDev.apiIA.repository;

import jabpDev.apiIA.entity.CobolRoutineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CobolRoutineRepository extends JpaRepository<CobolRoutineEntity, Long> {

    @Query(value = """
        SELECT *,
               embedding <=> CAST(:embedding AS vector) AS distance
        FROM cobol_routine
        ORDER BY distance
        LIMIT 5
        """, nativeQuery = true)
    List<CobolRoutineEntity> buscarSimilares(
            @Param("embedding") String embedding
    );

    @Query(value = """
            SELECT * FROM cobol_routine WHERE :programaChamado = ANY(calls)
            """, nativeQuery = true)
    List<CobolRoutineEntity> buscarQuemChamaOPrograma(@Param("programaChamado") String programaChamado);
}
