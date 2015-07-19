package core.neo4j;

import java.util.Set;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.CypherDslRepository;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface SubscribedRelationshipRepository extends GraphRepository<SubscribedRelationship>{
	
	@Query(value= "match (n{name:{0}})-[s]-(sn) where type(s) = 'SUBSCRIBED' return id(s) as id, startNode(s) as startNode, "
			+ "endNode(s) as endNode, s.score as score", elementClass=SubscribedRelationship.class)
	Set<SubscribedRelationship> findByUserNodeName(String name);
	
	@Query(value= "match (n)-[s]-(sn) where id(n) = {0} and type(s) = 'SUBSCRIBED' return id(s) as id, startNode(s) as startNode, "
			+ "endNode(s) as endNode, s.score as score", elementClass=SubscribedRelationship.class)
	Set<SubscribedRelationship> findByUserNodeId(Long nodeId);
}


