package jabpDev.apiIA.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "msg_ia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conversa")
    private Conversa conversa;

    @Column(name = "tipo")
    private String tipo;

    @Column(columnDefinition = "TEXT", name = "conteudo")
    private String conteudo;

    @Column(name = "criadoEm")
    private LocalDateTime criadoEm;


}
