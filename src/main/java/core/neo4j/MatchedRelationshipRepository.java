package core.neo4j;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface MatchedRelationshipRepository extends GraphRepository<MatchedRelationship>{
	@Query(value= "match (n)<-[m{matchedName:{1}}]-(mn) where id(n) = {0} and type(m) = 'MATCHED' return id(m) as id, startNode(m) as startNode, "
			+ "endNode(m) as endNode, m.matchedName as matchedName, m.like as like", elementClass=MatchedRelationship.class)	
	MatchedRelationship findByEndNodeIdAndMatchedName(Long nodeId, String matchedName);
}
