package core.mysql;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

public interface AuthorityRepository extends CrudRepository<Authority, Long> {

}