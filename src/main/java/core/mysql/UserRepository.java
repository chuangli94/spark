package core.mysql;


import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
	
	List<User> findByUsername(String username);
	
	User findByAccessToken(String accessToken);
	
	long count();
}
