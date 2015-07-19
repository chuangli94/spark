package core;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = { "core.mysql"})
public class MySqlConfig {
    @Value("${spring.datasource.driverClassName}")
    private String databaseDriverClassName;
 
    @Value("${spring.datasource.url}")
    private String datasourceUrl;
 
    @Value("${spring.datasource.username}")
    private String databaseUsername;
 
    @Value("${spring.datasource.password}")
    private String databasePassword;
	
	@Primary
	@Bean(name = "mysqlDataSource")
	public DataSource DataSource(){
		org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
        ds.setDriverClassName(databaseDriverClassName);
        ds.setUrl(datasourceUrl);
        ds.setUsername(databaseUsername);
        ds.setPassword(databasePassword);
        return ds;
	}
	
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
}
