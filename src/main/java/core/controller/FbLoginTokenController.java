package core.controller;

import java.io.Serializable;

import core.mysql.FbUserRepository;
import core.mysql.FbUser;
import core.response.FbGraphTokenResp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;

@Component
@RestController
@RequestMapping(value={"/logintokenfb"})
@EnableAuthorizationServer
@ComponentScan
public class FbLoginTokenController {
	
	@Autowired
	private TokenStore tokenStore;
	
	@Autowired
	private FbUserRepository fbUserRepo;
	
	@RequestMapping(method=RequestMethod.GET)
	public OAuth2AccessToken loginToken(@RequestHeader(value="FBAccessToken") String fbAccessToken, @RequestHeader(value="FBUserId") String fbUserId) {
		
		RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter jsonToPojo = new MappingJackson2HttpMessageConverter();
        jsonToPojo.getObjectMapper().setPropertyNamingStrategy(
                PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        restTemplate.getMessageConverters().add(jsonToPojo);
        FbGraphTokenResp fbGraphTokenResp;
        try {
        fbGraphTokenResp = restTemplate.getForObject("https://graph.facebook.com/me?access_token=" + fbAccessToken, FbGraphTokenResp.class);
        } catch (Exception e) {
        	return null;
        }
        
        if ((fbGraphTokenResp == null) || (!fbGraphTokenResp.getId().equals(fbUserId))){
        	return null;
        }
        
        List<FbUser> fbUsers = fbUserRepo.findByUserId(fbUserId);
		if (fbUsers.isEmpty() || fbUsers.size() != 1){
			return null;
		}
		
	    		  
	    Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
	    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
	
	    Map<String, String> requestParameters = new HashMap<>();
	    String clientId = "sparkclient";
	    boolean approved = true;
	    Set<String> scope = new HashSet<>();
	    scope.add("read");
	    Set<String> resourceIds = new HashSet<>();
	    resourceIds.add("db");
	    Set<String> responseTypes = new HashSet<>();
	    responseTypes.add("code");
	    Map<String, Serializable> extensionProperties = new HashMap<>();
	
	    OAuth2Request oAuth2Request = new OAuth2Request(requestParameters, clientId,
	            authorities, approved, scope,
	            resourceIds, null, responseTypes, extensionProperties);
	
	
	    User userPrincipal = new User(fbUserId, "", true, true, true, true, authorities);
	    
	    DefaultTokenServices tokenServices = new DefaultTokenServices();
	    tokenServices.setTokenStore(tokenStore);
	    
	    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);
	    OAuth2Authentication oAuth2 = new OAuth2Authentication(oAuth2Request, authenticationToken);
	    OAuth2AccessToken token = tokenServices.createAccessToken(oAuth2);
	    
		FbUser fbUser = fbUsers.get(0);
		fbUser.setAccessToken(token.getValue());
		fbUserRepo.save(fbUser);
		
	    return token;
	
	}
	
}
