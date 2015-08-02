package core.neo4j;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class UserNode {

	@GraphId 
	public Long id;
	
	@Indexed
	public String name;
	
	@Indexed
	public Double longitude;
	
	@Indexed
	public Double latitude;
	
	public UserNode(){}
	
	public UserNode(String name, Double longitude, Double latitude){
		
		this.name = name;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
}
