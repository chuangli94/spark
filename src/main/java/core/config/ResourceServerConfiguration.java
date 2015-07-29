package core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.web.bind.annotation.RestController;


@Configuration
@EnableResourceServer
@RestController
@ComponentScan
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter{
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		// @formatter:off
//		http.authorizeRequests().antMatchers("/login").permitAll().antMatchers("/logintoken").;
//		.antMatchers("/*").access("#oauth2.hasScope('read')");
		http
		.requestMatchers().antMatchers("/app/*")
		.and()
		.authorizeRequests()
		.anyRequest().access("#oauth2.hasScope('read')");

		// @formatter:on
		
	}
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId("db");
	}
}
