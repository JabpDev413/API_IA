package jabpDev.apiIA.entity;

import com.pgvector.PGvector;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "cobol_routine")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CobolRoutineEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String programa;

    private String nomeRotina;

    private String tipoRotina;

    @Column(columnDefinition = "TEXT")
    private String codigo;

    @Column(columnDefinition = "TEXT")
    private String descricaoErro;

    @Column(columnDefinition = "TEXT")
    private String causa;

    @Column(columnDefinition = "TEXT")
    private String solucao;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 768)
    @Column(columnDefinition = "vector(768)")
    private float [] embedding;

    private Float similarityScore;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "calls", columnDefinition = "text[]")
    private List<String> calls;       // Lista de outros programas chamados por esta rotina (CALL "PGM")

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "copys", columnDefinition = "text[]")
    private List<String> copys;       // Lista de copybooks importados/usados nesta rotina (COPY "COPYBOOK")

}
