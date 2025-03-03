package com.qu1cksave.qu1cksave_backend.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
public class Qu1cksaveBackendConfiguration {
    // https://stackoverflow.com/questions/67970207/using-hibernate-sessionfactory-with-the-jpa-entitymanager-together
    // - Link has code to get a HibernateTransactionManager
    // ERROR 1: Parameter 0 of constructor in
    //   com.qu1cksave.qu1cksave_backend.job.JobService required a bean named
    //   'entityManagerFactory' that could not be found.
    // - Maybe I should use JpaTransactionManager from https://www.marcobehler.com/guides/spring-transaction-management-transactional-in-depth
    //   -- SOLUTION
    // - https://stackoverflow.com/questions/48416927/spring-boot-required-a-bean-named-entitymanagerfactory-that-could-not-be-foun
    //   -- Something about how the factory you use has to be named
    //      entityManagerFactory
    //      + Can do @Bean(name = "entityManagerFactory")
    // ERROR 2:  Factory method 'dataSource' threw exception with message: URL
    //   must start with 'jdbc'
    // - Just change the url in the environment
    @Bean
    public JpaTransactionManager transactionManager(
        @Autowired EntityManagerFactory entityManagerFactory
        ) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

// NOTES:
// - I can autowire an entityManager and dataSource
// - Though, I'll need to get SessionFactory by unwrapping from entityManager
//   and creating a bean for it
//
// USEFUL LINKS:
// https://www.baeldung.com/hibernate-spring
// - Configuring a SessionFactory, DataSource, and TransactionManager
// https://www.marcobehler.com/guides/java-databases
// https://docs.jboss.org/hibernate/orm/6.6/quickstart/html_single/#hibernate-gsg-tutorial-annotations-test
// - I can use these two links to configure my own SessionFactory if needed
// https://stackoverflow.com/questions/65230311/configure-a-datasource-to-connect-to-a-managed-postgres-server-on-digital-ocea
// - If configuring my own DataSource, should I usePGSimpleDataSource?
//
// Code to get a SessionFactory:
// https://stackoverflow.com/questions/43895643/how-to-autowire-hibernate-sessionfactory-in-spring-boot
// - Using entityManager to get a sessionFactory
//@Bean
//public SessionFactory sessionFactory(
//    @Autowired EntityManager entityManager
//) {
//    Session session = entityManager.unwrap(Session.class);
//    SessionFactory sessionFactory = session.getSessionFactory();
//    return sessionFactory;
//}
