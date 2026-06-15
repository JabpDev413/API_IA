package jabpDev.apiIA.repository;

import jabpDev.apiIA.entity.HSTabelas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HSTabelasRepository extends JpaRepository<HSTabelas, Long> {
}
