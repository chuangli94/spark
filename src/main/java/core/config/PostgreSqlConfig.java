package core.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = { "core.postgresql" })
public class PostgreSqlConfig {
    @Value("${spring.datasource.driverClassName}")
    private String databaseDriverClassName;
 
    @Value("${spring.datasource.url}")
    private String datasourceUrl;
 
    @Value("${spring.datasource.username}")
    private String databaseUsername;
 
    @Value("${spring.datasource.password}")
    private String databasePassword;
    
	@Primary
	@Bean(name = "postgresqlDataSource")
	public DataSource dataSource(){
		org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
        ds.setDriverClassName(databaseDriverClassName);
        ds.setUrl(datasourceUrl);
        ds.setUsername(databaseUsername);
        ds.setPassword(databasePassword);
        return ds;
	}
}

	
//	@Bean
//	public LocalContainerEntityManagerFactoryBean tagEntityManagerFactory(
//	        EntityManagerFactoryBuilder builder) {
//	    return builder
//	            .dataSource(DataSource())
//	            .packages(Tag.class)
//	            .persistenceUnit("tags")
//	            .build();
//	}
//	
//	@Bean
//	public LocalContainerEntityManagerFactoryBean imageEntityManagerFactory(
//	        EntityManagerFactoryBuilder builder) {
//	    return builder
//	            .dataSource(DataSource())
//	            .packages(Image.class)
//	            .persistenceUnit("images")
//	            .build();
//	}
	
//	@Bean(name = "mysqlEntityManager")
//	public EntityManager entityManager(){
//		return entityManagerFactory().createEntityManager();
//	}
//	
//	@Bean(name = "mysqlEntityManagerFactory")
//	public EntityManagerFactory entityManagerFactory(){
//		LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
//		lef.setDataSource(mysqlDataSource());
//		lef.setJpaVendorAdapter(jpaVendorAdapter);
//		lef.setPackagesToScan("core.mysql");
//		lef.setPersistenceUnitName("mysqlPersistenceUnit");
//		lef.afterPropertiesSet();
//		return lef.getObject();
//	}
//	
//	@Bean(name = "mysqlTransactionManager")
//	public PlatformTransactionManager transactionManager(){
//		return new JpaTransactionManager(entityManagerFactory());
//	}

