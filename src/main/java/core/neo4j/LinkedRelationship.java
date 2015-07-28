package core.neo4j;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.QueryResult;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.ResultColumn;

@QueryResult
@RelationshipEntity(type="LINKED") 
public class LinkedRelationship {
	@GraphId
	@ResultColumn("id")
	public Long id;
	
	@ResultColumn("startNode")
	public UserNode startNode;
	
	@ResultColumn("endNode")
	public UserNode endNode;
}
