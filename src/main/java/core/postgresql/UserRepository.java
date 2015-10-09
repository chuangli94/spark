package core.postgresql;


import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface UserRepository extends CrudRepository<User, Long>, UserRepositoryCustom {
	
	List<User> findByUsername(String username);
	
	User findByAccessToken(String accessToken);
	
	long count();
	
	List<User> findRandomUser(String username, int rows);
}
