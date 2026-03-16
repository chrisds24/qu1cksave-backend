package com.qu1cksave.qu1cksave_backend;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.qu1cksave.qu1cksave_backend.filters.BearerAuthenticationFilter;
import com.qu1cksave.qu1cksave_backend.filters.ExceptionHandlerFilter;
import com.qu1cksave.qu1cksave_backend.filters.JWTFilter;
import com.qu1cksave.qu1cksave_backend.filters.MemberAuthorizationFilter;
import com.qu1cksave.qu1cksave_backend.filters.ReqBodySizeFilter;
import com.qu1cksave.qu1cksave_backend.user.UserService;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;

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

    @Bean
    public ExceptionHandlerFilter exceptionHandlerFilter() {
        return new ExceptionHandlerFilter();
    }

    @Bean
    public BearerAuthenticationFilter bearerAuthenticationFilter() {
        return new BearerAuthenticationFilter();
    }

    @Bean
    public JWTFilter jwtFilter(
        FirebaseAuth firebaseAuth,
        UserService userService
    ) {
        return new JWTFilter(firebaseAuth, userService);
    }

    @Bean
    public MemberAuthorizationFilter memberAuthorizationFilter() {
        return new MemberAuthorizationFilter();
    }

    @Bean
    public ReqBodySizeFilter reqBodySizeFilter() {
        return new ReqBodySizeFilter();
    }

    // Register filters
    // - https://www.baeldung.com/spring-boot-add-filter (Used this one)
    // - https://stackoverflow.com/questions/75117913/how-do-i-manually-register-filters-in-springboot
    // - https://springframework.guru/jwt-authentication-in-spring-microservices-jwt-token/
    @Bean
    public FilterRegistrationBean<ExceptionHandlerFilter> exceptionHandlerFilterRegistration(
        ExceptionHandlerFilter exceptionHandlerFilter
    ){
        FilterRegistrationBean<ExceptionHandlerFilter> registrationBean
            = new FilterRegistrationBean<>();

        registrationBean.setFilter(exceptionHandlerFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<BearerAuthenticationFilter> bearerAuthenticationFilterRegistration(
        BearerAuthenticationFilter bearerAuthenticationFilter
    ){
        FilterRegistrationBean<BearerAuthenticationFilter> registrationBean
            = new FilterRegistrationBean<>();

        registrationBean.setFilter(bearerAuthenticationFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(2);

        return registrationBean;
    }

    // Exclude an endpoint:
    // - https://www.baeldung.com/spring-exclude-filter
    @Bean
    public FilterRegistrationBean<JWTFilter> jwtFilterRegistration(
        JWTFilter jwtFilter
    ){
        FilterRegistrationBean<JWTFilter> registrationBean
            = new FilterRegistrationBean<>();

        registrationBean.setFilter(jwtFilter);
        // If there were login and signup endpoints, I could use
        //   shouldNotFilter in the filter itself to exclude them
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(3);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<MemberAuthorizationFilter> memberAuthorizationFilterRegistration(
        MemberAuthorizationFilter memberAuthorizationFilter
    ){
        FilterRegistrationBean<MemberAuthorizationFilter> registrationBean
            = new FilterRegistrationBean<>();

        registrationBean.setFilter(memberAuthorizationFilter);
        // If there were login and signup endpoints, I could use
        //   shouldNotFilter in the filter itself to exclude them
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(4);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<ReqBodySizeFilter> reqBodySizeFilterRegistration(
        ReqBodySizeFilter reqBodySizeFilter
    ){
        FilterRegistrationBean<ReqBodySizeFilter> registrationBean
            = new FilterRegistrationBean<>();

        registrationBean.setFilter(reqBodySizeFilter);
        // I excluded any requests not using PUT, POST, or PATCH using
        //   shouldNotFilter in the filter itself
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(5);

        return registrationBean;
    }

    @Bean
    public S3Client s3Client() {
        StaticCredentialsProvider staticCredentialsProvider =
            StaticCredentialsProvider.create(AwsBasicCredentials.create(
                System.getenv("BUCKET_ACCESS_KEY"),
                System.getenv("BUCKET_SECRET_ACCESS_KEY")
            ));

        return S3Client.builder()
            .region(Region.of(System.getenv("BUCKET_REGION")))
            .credentialsProvider(staticCredentialsProvider)
            .build()
        ;
    }

    @Bean
    public FirebaseAuth firebaseAuth() throws IOException {
        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.getApplicationDefault())
            // I don't think this is needed?
//            .setDatabaseUrl("https://<DATABASE_NAME>.firebaseio.com/")
            .build();

        FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);

        return FirebaseAuth.getInstance(firebaseApp);
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
