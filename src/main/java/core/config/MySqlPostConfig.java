package core.config;

import javax.persistence.EntityManager;

import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
//@EnableAutoConfiguration
//@ComponentScan("core.thread")
public class MySqlPostConfig{

	@Autowired
	LocalContainerEntityManagerFactoryBean entityManagerFactoryBean;
    
    @Bean(name="mysqlTransactionManager")
    public PlatformTransactionManager transactionManager(){
       JpaTransactionManager transactionManager = new JpaTransactionManager();
       transactionManager.setEntityManagerFactory(
        entityManagerFactoryBean.getObject() );
       
       return transactionManager;
    }   

}