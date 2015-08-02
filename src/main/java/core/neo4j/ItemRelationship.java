package core.neo4j;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.QueryResult;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.ResultColumn;

@QueryResult
@RelationshipEntity(type="ITEM") 
public class ItemRelationship {
	@GraphId
	@ResultColumn("id")
	public Long id;
	
	@ResultColumn("startNode")
	public UserNode startNode;
	
	@ResultColumn("endNode")
	public UserNode endNode;
	
	@Indexed
	@ResultColumn("itemName")
	public String itemName;
	
	@ResultColumn("like")
	public Integer like;
	
	public ItemRelationship(){
	}
	
	
	public void setId(Long id){
		this.id = id;
	}
	
	public Long getId(){
		return this.id;
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
	
	
	public void setLike(Integer like){
		this.like = like;
	}
	public Integer getLike(){
		return like;
	}
	
	public void setItemName(String itemName){
		this.itemName = itemName;
	}
	public String getItemName(){
		return itemName;
	}
	
	
}
