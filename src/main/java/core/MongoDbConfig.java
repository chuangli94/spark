package core;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = { "core.mongodb"})
public class MongoDbConfig {
	
    @Value("${mongodb.url}")
    private String url;
    
    @Value("${mongodb.database}")
    private String database;
    
    @Value("${mongodb.username}")
    private String username;
    
    @Value("${mongodb.password}")
    private String password;
    
	@Bean
	public MongoClientFactoryBean mongo() throws UnknownHostException {
		MongoCredential mongoCredentials = MongoCredential.createCredential(username,
                database,
                password.toCharArray());
		MongoClientFactoryBean mongo = new MongoClientFactoryBean();
		mongo.setHost(url);
		mongo.setCredentials(new MongoCredential[]{mongoCredentials});
		return mongo;
	}
}
