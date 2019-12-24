package com.javainterviewpoint.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@PropertySource("classpath:database.properties")
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.demo"})
@EnableJpaRepositories (basePackages = {"com.demo.repository"})
public class AppContext {
	
	@Autowired
    private Environment environment;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty("jdbc.driverClassName"));
        dataSource.setUrl(environment.getRequiredProperty("jdbc.url"));
        dataSource.setUsername(environment.getRequiredProperty("jdbc.username"));
        dataSource.setPassword(environment.getRequiredProperty("jdbc.password"));
        return dataSource;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
        properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
        properties.put("hibernate.format_sql", environment.getRequiredProperty("hibernate.format_sql"));
        properties.put("hibernate.hbm2ddl.auto", environment.getRequiredProperty("hibernate.hbm2ddl.auto"));
        return properties;
    }
   
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    	
    	LocalContainerEntityManagerFactoryBean emfbean = new LocalContainerEntityManagerFactoryBean();
    	emfbean.setPackagesToScan(new String[] {  "com.demo.entity" });
    	emfbean.setDataSource(dataSource());
    	emfbean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
    	emfbean.setJpaProperties(hibernateProperties());
       
        return emfbean;
    }

    @Bean("transactionManager")
    public JpaTransactionManager getTransactionManager() {
        
    	JpaTransactionManager transactionManager = new JpaTransactionManager();
    	
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        transactionManager.setDataSource(dataSource());
        
        return transactionManager;
    }
    

   
}
	