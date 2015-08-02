package core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RestController;


@Configuration
@RestController
@ComponentScan
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private TokenStore tokenStore;
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenStore(tokenStore);
	}
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		
		// @formatter:off
		clients.inMemory()
		.withClient("sparkclient")
		.authorizedGrantTypes("authorization_code")
		.authorities("ROLE_CLIENT")
		.scopes("read", "trust")
		.resourceIds("db")
		.redirectUris("http://anywhere?key=value")
		.secret("secret123")
		.and().
		withClient("my-client-with-secret")
		.authorizedGrantTypes("client_credentials", "password")
		.authorities("ROLE_CLIENT")
		.scopes("read")
		.resourceIds("db")
		.secret("secret");
		// @formatter:on
	}
	
}
