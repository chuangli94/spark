package core.controller;

import java.io.Serializable;
import core.mysql.UserRepository;
import core.mysql.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
@RequestMapping(value={"/logintoken"})
@EnableAuthorizationServer
@ComponentScan
public class LoginTokenController {
	
	@Autowired
	private TokenStore tokenStore;
	
	@Autowired
	UserRepository userRepo;
	
	
	@RequestMapping(method=RequestMethod.GET)
	public OAuth2AccessToken loginToken() {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
	    	
		// this should never really happen, as they just logged in and should have a username.
		if (username == null){
			return null;
		}
		List<core.mysql.User> users = userRepo.findByUsername(username);
		if (users.isEmpty() || users.size() != 1){
			return null;
		}
		
		
		// Token Generation
		
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
	
	
	    org.springframework.security.core.userdetails.User userPrincipal = new org.springframework.security.core.userdetails.User(username, "", true, true, true, true, authorities);
	    
	    DefaultTokenServices tokenServices = new DefaultTokenServices();
	    tokenServices.setTokenStore(tokenStore);
	    
	    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);
	    OAuth2Authentication oAuth2 = new OAuth2Authentication(oAuth2Request, authenticationToken);
	    OAuth2AccessToken token = tokenServices.createAccessToken(oAuth2);
	    
		core.mysql.User user = users.get(0);
		user.setAccessToken(token.getValue());
		userRepo.save(user);
	    return token;
	}
}
