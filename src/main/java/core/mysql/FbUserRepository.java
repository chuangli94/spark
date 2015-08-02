package core.mysql;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

public interface FbUserRepository extends CrudRepository<FbUser, Long>{

	List<FbUser> findByUserId(String userId) ;
	
}
