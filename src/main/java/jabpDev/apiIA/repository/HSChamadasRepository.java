package jabpDev.apiIA.repository;

import jabpDev.apiIA.entity.HSChamadas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HSChamadasRepository extends JpaRepository<HSChamadas,Long> {
    @Query(value = """
            select c.chamado
            from hs_chamadas c
            where c.chamador = :chamador
            """,nativeQuery = true)
    List<String> findByChamador(String chamador);
}
