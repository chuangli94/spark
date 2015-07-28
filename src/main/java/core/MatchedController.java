package core;

import java.util.Set;

import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import core.mongodb.UserDocument;
import core.mongodb.UserDocumentRepository;
import core.mysql.User;
import core.mysql.UserRepository;
import core.neo4j.ItemRelationship;
import core.neo4j.LinkedRelationship;
import core.neo4j.LinkedRelationshipRepository;
import core.neo4j.MatchedRelationship;
import core.neo4j.MatchedRelationshipRepository;
import core.neo4j.SubscribedRelationship;
import core.neo4j.UserNode;
import core.neo4j.UserNodeRepository;

@RestController
public class MatchedController {
	@Autowired
	UserRepository userRepo;
	@Autowired
	UserNodeRepository userNodeRepo;
	@Autowired
	MatchedRelationshipRepository matchedRelationshipRepo;
	@Autowired
	LinkedRelationshipRepository linkedRelationshipRepo;
	@Autowired
	GraphDatabase graphDatabase;
	@Autowired
	Neo4jTemplate neo4jTemplate;
	@Autowired
	UserDocumentRepository userDocumentRepo;
	
	@Transactional
	@RequestMapping(value="/pushmatched", method=RequestMethod.GET)
	public String pushMatched(@RequestHeader(value="Authorization") String token, @RequestHeader(value="Matched") String matched, @RequestHeader(value="Like") Integer like){
		User user = userRepo.findByAccessToken(token.substring("Bearer ".length()));
		String name = user.getUsername();
		UserDocument userDocument = userDocumentRepo.findByName(name);
		String serverItem = userDocument.deQueue();
		
		if (!serverItem.startsWith("matched/")){
			return "failure";
		}
		
		if (!serverItem.equals(matched)){
			return "failure";
		}
		
		Transaction tx = graphDatabase.beginTx();
		try {
			UserNode userNode = userNodeRepo.findByName(name);
			UserNode matchedNode = userNodeRepo.findByName(matched.substring(matched.indexOf("/") + 1));
			MatchedRelationship matchedRelationship = matchedRelationshipRepo.findByEndNodeIdAndMatchedName(userNode.id, matched);
			if (matchedRelationship == null){
				matchedRelationship = neo4jTemplate.createRelationshipBetween(userNode, matchedNode, MatchedRelationship.class, "MATCHED", true);
				matchedRelationship.setLike(like);
				matchedRelationship.setMatchedName("matched/" + name);
				matchedRelationshipRepo.save(matchedRelationship);
			} else {
				if ((matchedRelationship.like == 1) && (like == 1)){
					LinkedRelationship linkedRelationship = neo4jTemplate.createRelationshipBetween(userNode, matchedNode, LinkedRelationship.class, "LINKED", true);
					linkedRelationshipRepo.save(linkedRelationship);
				}
				matchedRelationshipRepo.delete(matchedRelationship);
			}
			userDocumentRepo.save(userDocument);
//			for (SubscribedRelationship matchedSubscribedRelationship: matchedSubscribedRelationships){
//				MatchedRelationship matchedRelationship = neo4jTemplate.createRelationshipBetween(matchedSubscribedRelationship.startNode, matchedSubscribedRelationship.endNode, MatchedRelationship.class, "MATCHED", true);
//				matchedRelationships.add(matchedRelationship);
//				if (userNode.id == matchedSubscribedRelationship.startNode.id){
//				matchedItemRelationships.addAll(itemRelationshipRepo.findItemsBetweenNodes(userNode.id, matchedSubscribedRelationship.endNode.id));
//				} else {
//					matchedItemRelationships.addAll(itemRelationshipRepo.findItemsBetweenNodes(userNode.id, matchedSubscribedRelationship.startNode.id));
//				}
//			}
//			
//			matchedRelationshipRepo.save(matchedRelationships);
		
		

		tx.success();
	} finally{
		tx.close();
	}
		return "success";
	}
}
