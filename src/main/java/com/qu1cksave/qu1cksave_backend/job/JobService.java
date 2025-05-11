package com.qu1cksave.qu1cksave_backend.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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

    // TODO: Remove later. For testing only
    @Transactional(readOnly = true)
    public ResponseJobDto getJob(UUID id) {
        return jobRepository.findById(id).map(JobMapper::toResponseDto).orElse(null);
    }

    // @Modifying annotation
    // - https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html#jpa.modifying-queries
    // - Only relevant when using @Query
    //   -- So this should go to a function in the repo if I have a
    //      modifying @Query
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

// ---------- Spring Data JPA Query Methods -------
// https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html

// ------------ Testing resources --------------

// TestRestTemplate vs. MockMvc vs RestAssured
// - https://stackoverflow.com/questions/52051570/whats-the-difference-between-mockmvc-restassured-and-testresttemplate
//   -- Seems like MockMvc only mocks the service and other layers
//      + Primarily for unit testing
//   -- TestRestTemplate and RestAssured are for integration testing
//   -- https://medium.com/swlh/https-medium-com-jet-cabral-testing-spring-boot-restful-apis-b84ea031973d
//      + Seems good
// - https://stackoverflow.com/questions/46732371/why-are-there-different-types-of-integration-tests-in-spring-boot
//   -- Also a good read
// - https://rieckpil.de/spring-boot-testing-mockmvc-vs-webtestclient-vs-testresttemplate/
//   -- The table is very helpful
//   -- Good example on how to use TestRestTemplate TODO: (5/3/25) I can use this example

// RestAssured
// - I keep seeing this one too
// - https://www.baeldung.com/rest-assured-tutorial

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

// TODO: (Later) Should AWS calls be mocked in integration tests?
// - https://www.reddit.com/r/aws/comments/lyano4/integration_testing_aws_services/


// Frontend/browser/end-to-end testing tools:
// - Cypress, Selenium (GOVX asked me for this)