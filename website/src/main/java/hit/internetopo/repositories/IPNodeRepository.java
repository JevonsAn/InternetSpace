package hit.internetopo.repositories;

import hit.internetopo.domain.IPNodeEntity;

import org.neo4j.springframework.data.repository.Neo4jRepository;

import java.util.List;
import java.util.Optional;

public interface IPNodeRepository extends Neo4jRepository<IPNodeEntity, String> {

    Optional<IPNodeEntity> findOneByIp(String ip);

//    List<IPNodeEntity> findAll();
}
