package jabpDev.apiIA.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "hs_tabelas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HSTabelas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "prefixo")
    private String prefixo;

    @Column(name = "sufixo")
    private String sufixo;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "programas_que_leem")
    private List<String> programasLeem;

    @Column(name = "programas_que_gravam")
    private List<String> programasGravam;
}
