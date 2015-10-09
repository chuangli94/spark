package core.postgresql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;


public interface UserRepositoryCustom {
		List<User> findRandomUser(String username, int rows);
}

class UserRepositoryImpl implements UserRepositoryCustom {
	
	@PersistenceContext
	private EntityManager em;
	
	@Override
	public List<User> findRandomUser(String username, int rows) {
		String sql = "SELECT u.username, u.profilePic FROM User u WHERE u.username != :username ORDER BY RAND()";
		Query query = em.createQuery(sql);
		query.setParameter("username", username);
		query.setMaxResults(rows);
		
		List<User> randUsers = new ArrayList<User>();
		
		List<Object[]> res = query.getResultList();
		for (Object[] row : res) {
			User user = new User();
			user.setUsername((String)row[0]);
			user.setProfilePic((String)row[1]);
			randUsers.add(user);
		}
		return randUsers;
	}
	
}
