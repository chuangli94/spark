package core.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetFederationTokenRequest;
import com.amazonaws.services.securitytoken.model.GetFederationTokenResult;

import core.mongodb.UserDocument;
import core.mongodb.UserDocumentRepository;
import core.mysql.Image;
import core.mysql.ImageRepository;
import core.mysql.User;
import core.mysql.UserRepository;
import core.neo4j.ItemRelationship;
import core.neo4j.ItemRelationshipRepository;
import core.neo4j.SubscribedRelationship;
import core.neo4j.SubscribedRelationshipRepository;
import core.neo4j.UserNode;
import core.neo4j.UserNodeRepository;
import core.response.QueueResp;
import core.wordutils.MultiplierCalculator;

@RestController
public class ItemController {
	@Autowired
	UserRepository userRepo;
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
	@Autowired
	UserDocumentRepository userDocumentRepo;
	@Autowired
	ImageRepository imageRepo;
	@Value("${awsbucketname}")
	private String awsBucketName;
	@Value("${awsstsendpoint}")
	private String awsSTSEndpoint;
	@Value("${awsfederateduserpolicy}")
	private String awsFederatedUserPolicy;

	@RequestMapping(value="/app/getitems")
	public QueueResp getItems(@RequestHeader(value="Authorization") String token){
		User user = userRepo.findByAccessToken(token.substring("Bearer ".length()));
		String name = user.getUsername();
		UserDocument userDocument = userDocumentRepo.findByName(name);
		List<String> queue = userDocument.getQueue();
		if (queue == null || queue.size() < 5) {
			List<String> newItems = new ArrayList<String>();
			Random r = new Random(System.currentTimeMillis());
			int startId = r.nextInt(((int)imageRepo.count()))-5;
			List<Image> listOfImages = imageRepo.findByIdBetween(startId, startId+4);
			for (Image image: listOfImages){
				newItems.add(image.getHash());
			}
			userDocument.enQueueAll(newItems);
			userDocumentRepo.save(userDocument);
		}
		return new QueueResp(userDocument.getQueue());
	}
	
	@RequestMapping(value="/app/gets3credentials", method=RequestMethod.GET)
	public Credentials getS3Credentials(@RequestHeader(value="Authorization") String token){
		User user = userRepo.findByAccessToken(token.substring("Bearer ".length()));
		String name = user.getUsername();
		AWSSecurityTokenServiceClient stsClient = new AWSSecurityTokenServiceClient();
		stsClient.setEndpoint(awsSTSEndpoint);
		GetFederationTokenRequest federationTokenRequest = new GetFederationTokenRequest();
		federationTokenRequest.setDurationSeconds(7200);
		federationTokenRequest.setPolicy(awsFederatedUserPolicy);
		federationTokenRequest.setName(name);
		GetFederationTokenResult federationTokenResult = stsClient.getFederationToken(federationTokenRequest);
		Credentials federationTokenCredentials = federationTokenResult.getCredentials();
		return federationTokenCredentials;
	}
	
	
	@RequestMapping(value="/app/pushitem", method=RequestMethod.GET)
	public String pushItem(@RequestHeader(value="Authorization") String token, @RequestHeader(value="Item") String item, @RequestHeader(value="Like") Integer like){
		User user = userRepo.findByAccessToken(token.substring("Bearer ".length()));
		String name = user.getUsername();
		UserDocument userDocument = userDocumentRepo.findByName(name);
		String serverItem = userDocument.deQueue();
		if (!serverItem.equals(item)){
			return "failure";
		}
		
		if (!serverItem.startsWith("item/")){
			return "failure";
		}
		
		Transaction tx = graphDatabase.beginTx();
		try {
			UserNode userNode = userNodeRepo.findByName(name);
			
			Set<ItemRelationship> itemRelationships = itemRelationshipRepo.findByEndNodeIdAndItemName(userNode.id, item);
			Set<SubscribedRelationship> subscribedRelationships = subscribedRelationshipRepo.findByUserNodeId(userNode.id);
//			subscribedRelationshipRepo.save(subscribedRelationships);
			List<SubscribedRelationship> updatedSubscribedRelationships = new ArrayList<SubscribedRelationship>();
			List<SubscribedRelationship> unmatchedSubscribedRelationships = new ArrayList<SubscribedRelationship>();
			List<SubscribedRelationship> matchedSubscribedRelationships = new ArrayList<SubscribedRelationship>();
			List<ItemRelationship> matchedItemRelationships = new ArrayList<ItemRelationship>();
			List<ItemRelationship> newItemRelationships = new ArrayList<ItemRelationship>();
			List<UserNode> itemBroadcastNodes = new ArrayList<UserNode>();
			List<String> matchedUserNames = new ArrayList<String>();
			for (SubscribedRelationship subscribedRelationship: subscribedRelationships){
				if (subscribedRelationship.startNode.id.equals(userNode.id)){
					itemBroadcastNodes.add(subscribedRelationship.endNode);
				} else {
					itemBroadcastNodes.add(subscribedRelationship.startNode);
				}
			}


			List<UserNode> deleteBroadcastNodes = new ArrayList<UserNode>();
			for (UserNode itemBroadcastNode : itemBroadcastNodes){
				for (ItemRelationship itemRelationship: itemRelationships){
					if (itemRelationship.startNode.id.equals(itemBroadcastNode.id)){
						deleteBroadcastNodes.add(itemBroadcastNode);
					}
				}
			}
			
			itemBroadcastNodes.removeAll(deleteBroadcastNodes);


			for (ItemRelationship itemRelationship: itemRelationships){
					if (like == 1 && itemRelationship.like == 1){
						UserNode otherNode = itemRelationship.startNode;

						for (SubscribedRelationship subscribedRelationship: subscribedRelationships){
							if (subscribedRelationship.startNode.id.equals(otherNode.id)){
								UserDocument otherDocument = userDocumentRepo.findByName(otherNode.name);

								float base = 1;
								float multiplier = MultiplierCalculator.calculateMultipler(userDocument.getCategoriesScore(), otherDocument.getCategoriesScore());
								subscribedRelationship.setScore(subscribedRelationship.getScore() + (base * multiplier));
								updatedSubscribedRelationships.add(subscribedRelationship);
							} else if (subscribedRelationship.endNode.id.equals(otherNode.id)){
								UserDocument otherDocument = userDocumentRepo.findByName(otherNode.name);
								float base = 1;
								float multiplier = MultiplierCalculator.calculateMultipler(userDocument.getCategoriesScore(), otherDocument.getCategoriesScore());
								subscribedRelationship.setScore(subscribedRelationship.getScore() + (base * multiplier));
								updatedSubscribedRelationships.add(subscribedRelationship);
							}
						}
					}
			}
			
			
			
			for (UserNode itemBroadcastNode: itemBroadcastNodes){
				ItemRelationship itemRelationship = neo4jTemplate.createRelationshipBetween(userNode, itemBroadcastNode, ItemRelationship.class, "ITEM", true);
				itemRelationship.setItemName(item);
				itemRelationship.setLike(like);
				newItemRelationships.add(itemRelationship);
			}
			
			for (SubscribedRelationship updatedSubscribedRelationship: updatedSubscribedRelationships){
				if (updatedSubscribedRelationship.getScore() >= 5){
					matchedSubscribedRelationships.add(updatedSubscribedRelationship);
					if (updatedSubscribedRelationship.startNode.id == userNode.id){
						matchedItemRelationships.addAll(itemRelationshipRepo.findItemsBetweenNodes(userNode.id, updatedSubscribedRelationship.endNode.id));
						matchedUserNames.add(updatedSubscribedRelationship.endNode.name);
					} else {
						matchedItemRelationships.addAll(itemRelationshipRepo.findItemsBetweenNodes(userNode.id, updatedSubscribedRelationship.startNode.id));
						matchedUserNames.add(updatedSubscribedRelationship.startNode.name);
					}
					

				} else {
					unmatchedSubscribedRelationships.add(updatedSubscribedRelationship);
				}
			}
			

			List<UserDocument> otherMatchedUserDocuments = userDocumentRepo.findByNameIn(matchedUserNames);
			for (UserDocument otherMatchedUserDocument : otherMatchedUserDocuments){
				otherMatchedUserDocument.enQueue("matched/" + userDocument.getName());
				userDocument.enQueue("matched/" + otherMatchedUserDocument.getName());
			}

			
			itemRelationshipRepo.delete(itemRelationships);
			itemRelationshipRepo.save(newItemRelationships);
			itemRelationshipRepo.delete(matchedItemRelationships);
			subscribedRelationshipRepo.save(unmatchedSubscribedRelationships);
			subscribedRelationshipRepo.delete(matchedSubscribedRelationships);
			userDocumentRepo.save(userDocument);
			userDocumentRepo.save(otherMatchedUserDocuments);

			tx.success();
		} finally{
			tx.close();
		}
		
		return "success";
	}
}
