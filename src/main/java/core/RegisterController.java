package core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;

@RestController
public class RegisterController {
	@Autowired
	UserRepository userRepo;
	@Autowired
	AuthorityRepository authRepo;
	@Autowired
	FbUserRepository fbUserRepo;
	
	
	@RequestMapping(value="/signup", method=RequestMethod.GET)
	public RegistrationResp registerUsernameAndPassword(@RequestHeader(value="Username") String username, @RequestHeader(value="Password") String password){
		if (userRepo.findByUsername(username).isEmpty()){
			User user = new User(username, password);
			Authority authority = new Authority(user, "ROLE_ADMIN");
			userRepo.save(user);
			authRepo.save(authority);
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