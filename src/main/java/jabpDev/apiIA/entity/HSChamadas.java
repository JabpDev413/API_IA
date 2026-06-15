package jabpDev.apiIA.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hs_chamadas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HSChamadas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chamador")
    private String chamador;

    @Column(name = "chamado")
    private String chamado;
}
