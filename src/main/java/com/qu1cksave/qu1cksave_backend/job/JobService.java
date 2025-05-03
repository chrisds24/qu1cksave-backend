package com.qu1cksave.qu1cksave_backend.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JobService {
    private final JobRepository jobRepository;

    // TODO: How does Spring, Spring MVC, Hibernate, and/or Spring Data JPA
    //   handle exceptions?
    //   - Since I want to return a JSON error object based on the error

    // If I need a property value, Ex:
    //   @Value("${postgres.host}") String postgresHost
    public JobService(@Autowired JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // Note: https://www.marcobehler.com/guides/spring-transaction-management-transactional-in-depth
    // - I'll need a JpaTransactionManager to use Transactional with Hibernate
    // https://stackoverflow.com/questions/10394857/how-to-use-transactional-with-spring-data
    // - Talks about @Transactional(readOnly = true) for select queries
    // https://docs.spring.io/spring-data/jpa/reference/jpa/transactions.html
    // - Talks about how having Transactional here causes the transaction configurations
    //   at the repositories to be neglected (which is what we want) and that
    //   the outer transaction configuration determines the actual one used.
    //   -- Marco Behler also mentioned this
    @Transactional(readOnly = true)
    public List<ResponseJobDto> getJobs(UUID userId) {
        // Saving this commented line for reference, where the repository
        //   returns a List<Job>, which gets converted to List<JobDto>
//        return jobRepository.findByMemberIdWithFiles(userId).stream().map(JobMapper::toDto).collect(Collectors.toList());
        return jobRepository.findByMemberIdWithFiles(userId);
    }

    // TODO: (5/3/25) I know there's something else I need for write
    //   transactions
    @Transactional
    public ResponseJobDto createJob(RequestJobDto newJob, UUID userId) {
        // 1: Create the resume (if there's one), returning the id (resumeId)
        // 2: Create the coverLetter, returning the id (coverLetterId)
        // 3: Create the job, using the associated file's id if it exists
        // 4: Add files to S3
        // At any point, if any of the steps above fail, the whole transaction
        //   should and will fail

        // Creating new entity
        // - https://spring.io/guides/gs/accessing-data-jpa
        // - https://docs.spring.io/spring-data/jpa/reference/jpa/entity-persistence.html
        Job newJobEntity = JobMapper.createEntity(newJob, userId);

        return JobMapper.toResponseDto(jobRepository.save(newJobEntity));
    }
}

// Convert string to UUID
//   UUID.fromString("1d27e3ee-1111-4e0d-ac0f-dadfcc420ce3"),

// ---------------- Testing resources --------------
// https://spring.io/guides/gs/testing-web
//
// https://medium.com/@mbanaee61/api-testing-in-spring-boot-2a6d69e5c3ce
// - For repositories that use Spring Data JPA, use @DataJpaTest to test the interaction with the database
//   -- In this example, we have a UserRepository that extends JpaRepository.
//      The @DataJpaTest annotation ensures that only the components related to JPA (
//      e.g., EntityManager, DataSource) are initialized, providing a lightweight testing environment.
// - Query Methods Tests: Verify that your custom query methods work as expected
// - +++++++++++++++++
// - Use @SpringBootTest:
//   -- Annotate your integration test classes with @SpringBootTest to load the complete Spring application context.
// - Database Integration Tests:
//   -- Test the interaction between your services and the database. Use an in-memory database or a test database for isolation.
//   -- @Sql("/data.sql") // Optional: Initialize test data using SQL scripts
// - TODO: (5/3/25) ++++++++++ ME +++++++++++++=
// - (5/3/25) I think I'll mostly need @SpringBootTest and not @DataJpaTest
// - I'll also only mock the AWS calls, but everything else should be integrated
//
// https://www.baeldung.com/spring-boot-testing
// - The application-integrationtest.properties contains the details to configure the persistence storage
//   -- So this is how we specify a test database
// - @DataJpaTest
//   -- @DataJpaTest provides some standard setup needed for testing the persistence layer
//   -- https://www.baeldung.com/junit-datajpatest-repository
//      + TODO: (5/3/25) Have a look at this regarding testing JPA queries
//
// https://www.reddit.com/r/learnjava/comments/1ial99t/testing_in_java/
// - Mockito
//   -- I generally use Mockito for tests that require a call to an external service. Stuff like AWS API calls
//
// https://www.youtube.com/watch?v=6uSnF6IuWIw TODO: (5/3/25) WATCH THIS !!!
// - Marco Behler on JUnit 5
//   -- Has stuff about BeforeAll, AfterAll, BeforeEach, AfterEach
//
// https://www.youtube.com/watch?v=JVPHSdHViMg
// - Marco Behler testing frameworks/libraries
//
// https://www.freecodecamp.org/news/unit-testing-services-endpoints-and-repositories-in-spring-boot-4b7d9dc2b772/
// - Nice example on how a request is made to an endpoint, but then the service
//   is mocked. So by the time the service is called, it's actually mocked
//   (NOTE: The endpoint is the one being tested)
//
// Spring Boot Docs:  https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html
// - TODO: (5/3/25) Read this if there's time

// Test Database Spring Boot TODO: (5/3/25)
// https://www.baeldung.com/spring-boot-testcontainers-integration-test
// - Seems really good, haven't read yet though
// https://www.baeldung.com/spring-testing-separate-data-source
// - Seems useful

// Test http request / Test endpoint spring boot TODO: (5/3/25)
// - TODO

// TODO: (5/3/25) TestRestTemplate vs. MockMvc

// OpenAPI specification:
// https://www.baeldung.com/spring-rest-openapi-documentation
// https://github.com/springdoc/springdoc-openapi

// Postman:
// https://medium.com/turkcell/spring-boot-rest-api-testing-with-postman-bb283b124416

// Swagger/OpenAPI vs Postman
// https://www.reddit.com/r/explainlikeimfive/comments/mtwi2r/eli5_software_development_what_is_the_difference/

// Controller tests, integration tests, and unit tests
// https://www.reddit.com/r/SpringBoot/comments/fd1qbu/controller_unit_tests_vs_integration_tests_in/
// https://www.reddit.com/r/rails/comments/iab5w3/what_is_the_difference_about_a_controller_test/
// https://www.reddit.com/r/node/comments/xhe6kj/how_do_you_guys_deal_with_unit_testing_against_a/

// Frontend/browser/end-to-end testing tools:
// - Cypress, Selenium (GOVX asked me for this)