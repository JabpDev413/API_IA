package jabpDev.apiIA.repository;

import jabpDev.apiIA.entity.Analise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnaliseRepository extends JpaRepository<Analise,Long> {
}
