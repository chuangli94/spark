package core;

import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	
	@Autowired
	DataSource ds;
    
	@Bean 
    public TokenStore tokenStore(){
		return new InMemoryTokenStore();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/logintoken", "/oauth/authorize").authenticated().and()
		.formLogin().failureUrl("/login?error")
		.defaultSuccessUrl("/logintoken")
		.loginPage("/login")
		.permitAll()
		.and()
        .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login")
        .permitAll()
        .and()
        .httpBasic()
        .and().csrf().disable();
	}
	
	/*
 	        http.authorizeRequests().antMatchers("/oauth/authorize").authenticated().and()
			.formLogin().failureUrl("/thirdpartylogin?error")
			.defaultSuccessUrl("/oauth/authorize")
			.loginPage("/thirdpartylogin")
			.permitAll()
			.and()
	        .logout().logoutRequestMatcher(new AntPathRequestMatcher("/thirdpartylogout")).logoutSuccessUrl("/thirdpartylogin")
	        .permitAll();(non-Javadoc)
	 * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder)
	 */
	
	
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
    	
        
        auth.jdbcAuthentication().dataSource(ds)
        .usersByUsernameQuery("select username, password, enabled from users where username=?")
        .authoritiesByUsernameQuery("select username, authority from authorities where username=?");
        
    }
}
