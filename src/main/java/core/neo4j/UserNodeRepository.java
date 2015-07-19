package core.neo4j;

import java.util.Set;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface UserNodeRepository extends GraphRepository<UserNode> {
	
	@Query("match (n) where (n.name = {0}) return n as UserNode")
	UserNode findByName(String name);
	
	//stupidest shit ever cause between doesn't work
	@Query(value="match (n) where (n.longitude >= {0} AND n.longitude <= {1}) AND (n.latitude >= {2} AND n.latitude <= {3}) return n as UserNode", elementClass=UserNode.class)
	Set<UserNode> findByLongitudeGreaterThanAndLongitudeLessThanAndLatitudeGreaterThanAndLatitudeLessThan(
			Double fromLongitude, Double toLongitude, Double fromLatitude, Double toLatitude);
	
//	
//	List<UserNode> findByLongitude(Double longitude);
	
//	List<UserNode> findByLatitudeBetween(Double fromLatitude, Double toLatitude);
	
//	Iterable<UserNode> findBySubscribedNodes(String name);
}
