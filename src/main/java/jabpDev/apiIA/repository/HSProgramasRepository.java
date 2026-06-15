package jabpDev.apiIA.repository;

import jabpDev.apiIA.entity.HSProgramas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HSProgramasRepository extends JpaRepository<HSProgramas,Long> {
}
