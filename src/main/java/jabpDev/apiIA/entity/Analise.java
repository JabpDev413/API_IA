package jabpDev.apiIA.entity;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "anali_ia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Analise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conversa")
    private Conversa conversa;

    @Column(name = "descricaoErro", columnDefinition = "TEXT")
    private String descricaoErro;

    @Column(name = "causa", columnDefinition = "TEXT")
    private String causa;

    @Column(name = "solucao", columnDefinition = "TEXT")
    private String solucao;

    @Column(name = "analiseTecnica", columnDefinition = "TEXT")
    private String analiseTecnica;

    @Column(name = "origem")
    private String origem;
}
