package jabpDev.apiIA.repository;

import jabpDev.apiIA.entity.HSMensagens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HSMensagensRepository extends JpaRepository<HSMensagens,Long> {

    @Query(value = """
            select m.programas
            from hs_mensagens m
            WHERE m.texto ILIKE TRIM(:texto)
            """, nativeQuery = true)
    List<String> buscaProgramas(String texto);
}
