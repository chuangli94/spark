package core.neo4j;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.QueryResult;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.ResultColumn;

@QueryResult
@RelationshipEntity(type="SUBSCRIBED") 
public class SubscribedRelationship {
	@GraphId
	@ResultColumn("id")
	public Long id;
	
	@ResultColumn("startNode")
	public UserNode startNode;
	
	@ResultColumn("endNode")
	public UserNode endNode;

	@ResultColumn("score")
	public float score;
	
	public SubscribedRelationship(){
	}
	
	public SubscribedRelationship(UserNode startNode, UserNode endNode, Integer score){
		
		this.startNode = startNode;
		this.endNode = endNode;
		this.score = score;
	}
	
	public void setId(Long id){
		this.id = id;
	}
	
	public Long getId(){
		return id;
	}
	
	public void setScore(float score){
		this.score = score;
	}
	
	public float getScore(){
		return score;
	}
	public void setEndNode(UserNode endNode){
		this.endNode = endNode;
	}
	public UserNode getEndNode(){
		return endNode;
	}
	public void setStartNode(UserNode startNode){
		this.startNode = startNode;
	}
	public UserNode getStartNode(){
		return startNode;
	}

}
