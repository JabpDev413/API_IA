package jabpDev.apiIA.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "hs_programas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HSProgramas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "modulo")
    private String modulo;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "tem_ficha")
    private Boolean temFicha;

    @Column(name = "tem_fluxo")
    private Boolean temFluxo;

    @Column(name = "n_chama")
    private Integer nChama;

    @Column(name = "n_chamado_por")
    private Integer nChamadoPor;

    @Column(name = "tier")
    private Integer tier;

    @Column(name = "tamanho_linhas")
    private Integer tamanhoLinhas;

    @Column(name = "n_arquivos")
    private Integer nArquivos;

    @Column(name = "arquivos")
    private List<String> arquivos;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}
