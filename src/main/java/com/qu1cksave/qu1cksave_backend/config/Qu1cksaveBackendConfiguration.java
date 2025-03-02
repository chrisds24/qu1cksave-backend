package com.qu1cksave.qu1cksave_backend.config;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;

@Configuration
public class Qu1cksaveBackendConfiguration {
    // https://www.baeldung.com/hibernate-spring
    // - Configuring a SessionFactory, DataSource, and TransactionManager
//    @Bean
//    public SessionFactory sessionFactory() {
//        // https://www.marcobehler.com/guides/java-databases
//        // https://docs.jboss.org/hibernate/orm/6.6/quickstart/html_single/#hibernate-gsg-tutorial-annotations-test
//        // - I combined code from the links above to get a SessionFactory
//        final StandardServiceRegistry registry =
//            new StandardServiceRegistryBuilder()
//                .build();
////        try {
//            MetadataSources sources = new MetadataSources(registry);
//            sources.addAnnotatedClass(Job.class);
//            sources.addAnnotatedClass(User.class);
//            sources.addAnnotatedClass(Resume.class);
//            sources.addAnnotatedClass(CoverLetter.class);
//            sources.addAnnotatedClass(YearMonthDate.class);
//            Metadata metadata = sources.buildMetadata();
//            SessionFactory sessionFactory = metadata.buildSessionFactory();
//            return sessionFactory;
////        } catch (Exception e) {
//            // The registry would be destroyed by the SessionFactory, but we
//            // had trouble building the SessionFactory so destroy it manually.
////            StandardServiceRegistryBuilder.destroy(registry);
////        }
//    }

    // https://stackoverflow.com/questions/43895643/how-to-autowire-hibernate-sessionfactory-in-spring-boot
    // - Using entityManager to get a sessionFactory
    @Bean
    public SessionFactory sessionFactory(
        @Autowired EntityManager entityManager
        ) {
        Session session = entityManager.unwrap(Session.class);
        SessionFactory sessionFactory = session.getSessionFactory();
        return sessionFactory;
    }

//    @Bean
//    public DataSource dataSource(
//        @Value("${spring.datasource.url}") String url,
//        @Value("${spring.datasource.username}") String username,
//        @Value("${spring.datasource.password}") String password
//    ) {
//        // https://stackoverflow.com/questions/65230311/configure-a-datasource-to-connect-to-a-managed-postgres-server-on-digital-ocea
//        // - PGSimpleDataSource
//        BasicDataSource dataSource = new BasicDataSource();
//        dataSource.setDriverClassName("org.h2.Driver");
//        dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
//        dataSource.setUsername("sa");
//        dataSource.setPassword("sa");
//
//        return dataSource;
//    }

//    @Bean
//    public PlatformTransactionManager hibernateTransactionManager() {
//        HibernateTransactionManager transactionManager
//            = new HibernateTransactionManager();
//        transactionManager.setSessionFactory(sessionFactory().getObject());
//        return transactionManager;
//    }
//
//    private final Properties hibernateProperties() {
//        Properties hibernateProperties = new Properties();
//        hibernateProperties.setProperty(
//            "hibernate.hbm2ddl.auto", "create-drop");
//        hibernateProperties.setProperty(
//            "hibernate.dialect", "org.hibernate.dialect.H2Dialect");
//
//        return hibernateProperties;
//    }

    // https://stackoverflow.com/questions/67970207/using-hibernate-sessionfactory-with-the-jpa-entitymanager-together
    // - Has code to get a HibernateTransactionManager
    @Bean
    public HibernateTransactionManager transactionManager(
        @Autowired SessionFactory sessionFactory
//        @Autowired DataSource dataSource // Autowiring works
    ) {
//        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
//        transactionManager.setSessionFactory(sessionFactory().getObject());
//        return transactionManager;
        return new HibernateTransactionManager(sessionFactory);
    }
}
