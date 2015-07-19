package core;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
//@EnableNeo4jRepositories(basePackages="core")
public class Application /*extends Neo4jConfiguration*/ {
	
//	public Application(){
//		setBasePackage(Application.class.getPackage().getName());
//	}
//	
//	@Bean(destroyMethod="shutdown")
//	public static GraphDatabaseService graphDatabaseService(@Value("${neo4j.url}") String neo4jUrl, @Value("${neo4j.username}") String neo4jUsername, @Value("${neo4j.password}")
//	String neo4jPassword) {
////	    return new SpringCypherRestGraphDatabase(neo4jUrl, neo4jUsername, neo4jPassword);
//		return new GraphDatabaseFactory().newEmbeddedDatabase("accessingdataneo4j.db");
//	}
	
//	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
}
