package core.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import core.event.BroadcastItemEvent;
import core.mongodb.UserDocument;
import core.mongodb.UserDocumentRepository;
import core.neo4j.ItemRelationship;
import core.neo4j.ItemRelationshipRepository;
import core.neo4j.SubscribedRelationship;
import core.neo4j.SubscribedRelationshipRepository;
import core.neo4j.UserNode;
import core.neo4j.UserNodeRepository;

//Not really pure neo4j cause theres mongodb here
@Service
public class Neo4jTaskListener {
	@Autowired
	UserDocumentRepository userDocumentRepo;
	@Autowired
	UserNodeRepository userNodeRepo;
	@Autowired
	ItemRelationshipRepository itemRelationshipRepo;
	@Autowired
	SubscribedRelationshipRepository subscribedRelationshipRepo;
	@Autowired
	GraphDatabase graphDatabase;
	@Autowired
	Neo4jTemplate neo4jTemplate;
	
	@EventListener
	public void handleBroadcastItemEvent(BroadcastItemEvent broadcastItemEvent){
		
		String name = broadcastItemEvent.getName();
		String itemName = broadcastItemEvent.getItemName();
		List<String> finalCategories = broadcastItemEvent.getFinalCategories();
		Integer like = 1;
		
		UserDocument userDocument = userDocumentRepo.findByName(name);
		if (!(finalCategories == null) && !(finalCategories.isEmpty())){
			for (String finalCategory: finalCategories){
				userDocument.addCategoryScore(finalCategory, 1);
			}
		}
		
		userDocumentRepo.save(userDocument);
		
		Transaction tx = graphDatabase.beginTx();
		try {
			UserNode userNode = userNodeRepo.findByName(name);
			
			Set<SubscribedRelationship> subscribedRelationships = subscribedRelationshipRepo.findByUserNodeId(userNode.id);			
//			subscribedRelationshipRepo.save(subscribedRelationships);
			List<SubscribedRelationship> updatedSubscribedRelationships = new ArrayList<SubscribedRelationship>();
			List<ItemRelationship> newItemRelationships = new ArrayList<ItemRelationship>();
			List<UserNode> itemBroadcastNodes = new ArrayList<UserNode>();
			List<String> broadcastNodeNames = new ArrayList<String>();
			for (SubscribedRelationship subscribedRelationship: subscribedRelationships){
				if (subscribedRelationship.startNode.id.equals(userNode.id)){
					itemBroadcastNodes.add(subscribedRelationship.endNode);
					broadcastNodeNames.add(subscribedRelationship.endNode.name);
				} else {
					itemBroadcastNodes.add(subscribedRelationship.startNode);
					broadcastNodeNames.add(subscribedRelationship.startNode.name);
				}
			}
			
			
			for (UserNode itemBroadcastNode: itemBroadcastNodes){
				ItemRelationship itemRelationship = neo4jTemplate.createRelationshipBetween(userNode, itemBroadcastNode, ItemRelationship.class, "ITEM", true);
				itemRelationship.setItemName(itemName);
				itemRelationship.setLike(like);
				newItemRelationships.add(itemRelationship);
			}
			
			
			List<UserDocument> usersToQueue = userDocumentRepo.findByNameIn(broadcastNodeNames);
			for (UserDocument userToQueue: usersToQueue){
				userToQueue.enQueue(itemName);
			}
			
			itemRelationshipRepo.save(newItemRelationships);
			subscribedRelationshipRepo.save(updatedSubscribedRelationships);
			userDocumentRepo.save(usersToQueue);
			
			tx.success();
            System.out.println("Done Nodes and Relationships!");     
		} catch (Exception e){
			System.out.println(e.getMessage());
		}
		finally{
			tx.close();
		}
	}
}
