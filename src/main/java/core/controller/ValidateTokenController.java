package core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
@RequestMapping(value={"/app/refreshtoken"})
@EnableAuthorizationServer
@ComponentScan
public class ValidateTokenController {

	@RequestMapping(method=RequestMethod.GET)
	public String validate(){
		return "success";
	}
	
}
