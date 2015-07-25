package core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import core.mongodb.UserDocument;
import core.mongodb.UserDocumentRepository;
import core.mysql.Authority;
import core.mysql.AuthorityRepository;
import core.mysql.FbUser;
import core.mysql.FbUserRepository;
import core.mysql.User;
import core.mysql.UserRepository;
import core.neo4j.SubscribedRelationship;
import core.neo4j.SubscribedRelationshipRepository;
import core.neo4j.UserNode;
import core.neo4j.UserNodeRepository;

@RestController
public class RegisterController {
	@Autowired
	UserRepository userRepo;
	@Autowired
	AuthorityRepository authRepo;
	@Autowired
	FbUserRepository fbUserRepo;
	@Autowired
	UserNodeRepository userNodeRepo;
	@Autowired
	SubscribedRelationshipRepository subscribedRelationshipRepo;
	@Autowired
	GraphDatabase graphDatabase;
	@Autowired
	Neo4jTemplate neo4jTemplate;
	@Autowired
	UserDocumentRepository userDocumentRepo;
	@Value("${awsuseruploadbucketname}")
	private String awsBucketName;

	
	@Transactional
	@RequestMapping(value="/signup", method=RequestMethod.GET)
	public RegistrationResp registerUsernameAndPassword(@RequestHeader(value="Username") String username, 
			@RequestHeader(value="Password") String password, @RequestHeader(value="Longitude") Double longitude, 
			@RequestHeader(value="Latitude") Double latitude, @RequestHeader(value="GcmRegId") String gcmRegId){
		
		if (userRepo.findByUsername(username).isEmpty()){
			User user = new User(username, password, gcmRegId);
			Authority authority = new Authority(user, "ROLE_ADMIN");
			userRepo.save(user);
			authRepo.save(authority);
			
			UserDocument userDocument = new UserDocument(username, new ArrayList<String>(), new ArrayList<String>());
			userDocumentRepo.save(userDocument);

			Transaction tx = graphDatabase.beginTx();			
			try {
				
					UserNode userNode = new UserNode(username, longitude, latitude);
					Set<UserNode> nodesToSubscribe =
							userNodeRepo.findByLongitudeGreaterThanAndLongitudeLessThanAndLatitudeGreaterThanAndLatitudeLessThan(
									longitude - 10, longitude + 10, latitude - 10, latitude + 10);
					userNodeRepo.save(userNode);
					Set<SubscribedRelationship> subscribedRelationships = new HashSet<SubscribedRelationship>();
					for (UserNode nodeToSubscribe : nodesToSubscribe){
						
						SubscribedRelationship subscribedRelationship = neo4jTemplate.createRelationshipBetween(userNode, nodeToSubscribe, SubscribedRelationship.class, "SUBSCRIBED", true);
	//					SubscribedRelationship subscribedRelationship = new SubscribedRelationship(userNode, nodeToSubscribe, 0);
						subscribedRelationship.setScore(0);
						subscribedRelationships.add(subscribedRelationship);
					}
					subscribedRelationshipRepo.save(subscribedRelationships);
				
				tx.success();
			} finally {
				tx.close();
			}
			
			return new RegistrationResp("success");
		} else return new RegistrationResp("duplicate");
	}
	
	@RequestMapping(value="/signupfb", method=RequestMethod.GET)
	public RegistrationResp registerFb(@RequestHeader(value="FBAccessToken") String fbAccessToken, @RequestHeader(value="FBUserId") String fbUserId){
		
		RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter jsonToPojo = new MappingJackson2HttpMessageConverter();
        jsonToPojo.getObjectMapper().setPropertyNamingStrategy(
                PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        restTemplate.getMessageConverters().add(jsonToPojo);
        FbGraphTokenResults fbGraphTokenResults;
        try {
        fbGraphTokenResults = restTemplate.getForObject("https://graph.facebook.com/me?access_token=" + fbAccessToken, FbGraphTokenResults.class);
        } catch (Exception e) {
        	return new RegistrationResp("unauthorized");
        }
        
        if ((fbGraphTokenResults == null) || (!fbGraphTokenResults.getId().equals(fbUserId))){
        	return new RegistrationResp("unauthorized");
        }
        
		if (fbUserRepo.findByUserId(fbUserId).isEmpty()){
			FbUser fbUser = new FbUser(fbUserId);
			fbUserRepo.save(fbUser);
			return new RegistrationResp("success");	
		} else return new RegistrationResp("duplicate");
	}
}