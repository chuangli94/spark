package core;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface FbUserRepository extends CrudRepository<FbUser, Long>{

	List<FbUser> findByUserId(String userId) ;
	
}
