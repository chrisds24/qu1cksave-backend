package com.qu1cksave.qu1cksave_backend;

import com.qu1cksave.qu1cksave_backend.filters.APIKeyFilter;
import com.qu1cksave.qu1cksave_backend.filters.BearerAuthenticationFilter;
import com.qu1cksave.qu1cksave_backend.filters.ExceptionHandlerFilter;
import com.qu1cksave.qu1cksave_backend.filters.JWTFilter;
import com.qu1cksave.qu1cksave_backend.filters.MemberAuthorizationFilter;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.springframework.transaction.annotation.RollbackOn.ALL_EXCEPTIONS;

@Configuration
@EnableJpaRepositories
// https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html
// - To rollback on all exceptions, not just runtime
@EnableTransactionManagement(rollbackOn=ALL_EXCEPTIONS)
public class Qu1cksaveBackendConfiguration {
    // https://stackoverflow.com/questions/67970207/using-hibernate-sessionfactory-with-the-jpa-entitymanager-together
    // - Link has code to get a HibernateTransactionManager
    // ERROR 1 (SOLVED)
    //   Parameter 0 of constructor in com.qu1cksave.qu1cksave_backend.job.JobService required a bean named 'entityManagerFactory' that could not be found.
    //   - Maybe I should use JpaTransactionManager from https://www.marcobehler.com/guides/spring-transaction-management-transactional-in-depth
    //     -- This is the SOLUTION
    //   - https://stackoverflow.com/questions/48416927/spring-boot-required-a-bean-named-entitymanagerfactory-that-could-not-be-foun
    //     -- Something about how the factory you use has to be named
    //        entityManagerFactory
    //        + Can do @Bean(name = "entityManagerFactory")
    // ERROR 2 (SOLVED)
    //   Factory method 'dataSource' threw exception with message: URL must start with 'jdbc'
    //   - SOLUTION: Just change the url in the environment
    //
    // https://docs.spring.io/spring-data/jpa/reference/repositories/create-instances.html
    // - Source for the transaction manager code below
    @Bean
    public PlatformTransactionManager transactionManager(@Autowired EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }

    // Register filters
    // - https://www.baeldung.com/spring-boot-add-filter (Used this one)
    // - https://stackoverflow.com/questions/75117913/how-do-i-manually-register-filters-in-springboot
    // - https://springframework.guru/jwt-authentication-in-spring-microservices-jwt-token/
    @Bean
    public FilterRegistrationBean<ExceptionHandlerFilter> exceptionHandlerFilter(){
        FilterRegistrationBean<ExceptionHandlerFilter> registrationBean
            = new FilterRegistrationBean<>();

        registrationBean.setFilter(new ExceptionHandlerFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<BearerAuthenticationFilter> bearerAuthenticationFilter(){
        FilterRegistrationBean<BearerAuthenticationFilter> registrationBean
            = new FilterRegistrationBean<>();

        registrationBean.setFilter(new BearerAuthenticationFilter());
        // TODO: Later, this should not apply to login and signup endpoints
        //  This would also be order 3, with APIKeyFilter being order 2
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(2);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<APIKeyFilter> apiKeyFilter(){
        FilterRegistrationBean<APIKeyFilter> registrationBean
            = new FilterRegistrationBean<>();

        registrationBean.setFilter(new APIKeyFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(3);

        return registrationBean;
    }

    // Exclude an endpoint:
    // - https://www.baeldung.com/spring-exclude-filter
    @Bean
    public FilterRegistrationBean<JWTFilter> jwtFilter(){
        FilterRegistrationBean<JWTFilter> registrationBean
            = new FilterRegistrationBean<>();

        registrationBean.setFilter(new JWTFilter());
        // I excluded login and signup using shouldNotFilter in the filter itself
//        registrationBean.addUrlPatterns("*");
        registrationBean.addUrlPatterns("/job/*", "/resume/*", "/coverLetter/*");
        registrationBean.setOrder(4);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<MemberAuthorizationFilter> memberAuthorizationFilter(){
        FilterRegistrationBean<MemberAuthorizationFilter> registrationBean
            = new FilterRegistrationBean<>();

        registrationBean.setFilter(new MemberAuthorizationFilter());
        // I excluded login and signup using shouldNotFilter in the filter itself
//        registrationBean.addUrlPatterns("*");
        registrationBean.addUrlPatterns("/job/*", "/resume/*", "/coverLetter/*");
        registrationBean.setOrder(5);

        return registrationBean;
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
