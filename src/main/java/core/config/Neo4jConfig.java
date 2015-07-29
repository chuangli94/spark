package core.config;

import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.rest.SpringRestGraphDatabase;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableNeo4jRepositories(basePackages= {"core.neo4j"})
public class Neo4jConfig extends Neo4jConfiguration{
	
	
	public Neo4jConfig(){
	setBasePackage("core.neo4j");
	}

	@Bean()
	public GraphDatabaseService graphDatabaseService(@Value("${neo4j.url}") String neo4jUrl, @Value("${neo4j.username}") String neo4jUsername, @Value("${neo4j.password}")
	String neo4jPassword) {
	    return new SpringRestGraphDatabase(neo4jUrl, neo4jUsername, neo4jPassword);
	}
}