package core.neo4j;

import java.util.Set;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface ItemRelationshipRepository extends GraphRepository<ItemRelationship> {
	@Query(value= "match (n{name:{0}})<-[i{itemName:{1}}]-(in) where type(i) = 'ITEM' return id(i) as id, startNode(i) as startNode, "
			+ "endNode(i) as endNode, i.itemName as itemName, i.like as like", elementClass=ItemRelationship.class)
	Set<ItemRelationship> findByEndNodeNameAndItemName(String name, String itemName);
	
	@Query(value= "match (n)<-[i{itemName:{1}}]-(in) where id(n) = {0} and type(i) = 'ITEM' return id(i) as id, startNode(i) as startNode, "
			+ "endNode(i) as endNode, i.itemName as itemName, i.like as like", elementClass=ItemRelationship.class)	
	Set<ItemRelationship> findByEndNodeIdAndItemName(Long nodeId, String itemName);
	@Query(value= "match (n)-[i]-(in) where id(n) = {0} and id(in) = {1} and type(i) = 'ITEM' return id(i) as id, startNode(i) as startNode, "
			+ "endNode(i) as endNode, i.itemName as itemName, i.like as like", elementClass=ItemRelationship.class)
	Set<ItemRelationship> findItemsBetweenNodes(Long nodeId, Long nodeId2);
}
