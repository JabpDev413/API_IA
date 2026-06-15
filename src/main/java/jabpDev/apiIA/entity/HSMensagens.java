package jabpDev.apiIA.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "hs_mensagens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HSMensagens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "texto",columnDefinition = "TEXT", unique = true)
    private String texto;

    @Column(name = "severidade")
    private String severidade;

    @Column(name = "n_ocorrencias")
    private Integer nOcorrencias;

    @Column(name = "programas")
    private List<String> programas;

    @Column(name = "primeira_aparicao_programa")
    private String primeiraAparicaoPrograma;

    @Column(name = "primeira_aparicao_linha")
    private Integer primeiraAparicaoLinha;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}
