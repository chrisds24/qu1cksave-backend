package com.qu1cksave.qu1cksave_backend.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class JobService {
    private final JobRepository jobRepository;

    // TODO: How does Spring, Spring MVC, Hibernate, and/or Spring Data JPA
    //   handle exceptions?
    //   - Since I want to return a JSON error object based on the error
    //   - Look at @ControllerAdvice

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
        // 1: Create the resume (if there's one), returning the id (resumeId)   TODO
        // 2: Create the coverLetter, returning the id (coverLetterId)          TODO
        // 3: Create the job, using the associated file's id if it exists
        // 4: Add files to S3                                                   TODO
        // At any point, if any of the steps above fail, the whole transaction
        //   should and will fail
        // 5: Return the job with file metadata included

        // Creating new entity
        // - https://spring.io/guides/gs/accessing-data-jpa
        // - https://docs.spring.io/spring-data/jpa/reference/jpa/entity-persistence.html
        Job newJobEntity = JobMapper.createEntity(newJob, userId);

        return JobMapper.toResponseDto(jobRepository.save(newJobEntity));
    }

    @Transactional
    public ResponseJobDto editJob(UUID id, UUID userId, RequestJobDto editJob) {
        // 1.) Get job from database
        //     - We'll use the resumeId and coverLetterId from this since the
        //       frontend job could have stale data for those
        //     - TODO: (5/13/25) What's the most appropriate way to handle a
        //        situation like this? What are all the situations where there
        //        could be stale data?
        //        -- Ex. jobA in tab1 and tab2 in same state, has resume
        //        -- Edit in tab2 to not have resume
        //        -- Tab1 now has stale resume data
        //        -- Edit resume in tab1 (still has resumeId, which should no
        //           longer exists
        //        -- If we used the stale resumeId from tab1, we won't be able
        //           to update resume related data.
        //        Even worse is if a resume was added again in tab2, so now
        //        the job has a different resumeId instead of nothing
        //        -- If we save the job in tab1, it could update the resumeId
        //           to be the one in tab1, so we lose the actual resume file
        //           in S3 associated with the resumeId in tab2
        // 2.) Query resume table (add, edit, delete, or do nothing)
        // 3.) Query cover letter table
        // 4.) Edit job
        // 5.) Make S3 calls for resume and cover letter
        // 6.) Return the job with resume and cover letter metadata attached

        // 1.) Get the job
        ResponseJobDto responseJobDto = jobRepository.findById(id).
            map(JobMapper::toResponseDto).orElse(null);

        if (responseJobDto == null) { // Job not found
            return null;
        }

        // Not needed since I'll be editing this obtained job, then calling
        //   save to update it in the db
//        UUID resumeId = responseJobDto.getResumeId();
//        UUID coverLetterId = responseJobDto.getCoverLetterId();

        // Mismatch between resumeId from frontend job (editJob) and database
        //   job, so editJob is stale. So don't allow edit and return an
        //   error instead
        // TODO: I should throw an exception instead, which is more informative
        //  But this will work for now
        if (!Objects.equals(editJob.getResumeId(), resumeId) ||
            !Objects.equals(editJob.getCoverLetterId(), coverLetterId)
        ) {
            return null;
        }

        // TODO: 2.) Query resume table

        // TODO: 3.) Query cover letter table

        // 4.) Edit job
        // - Just editing all editable fields
        //  title, companyName, jobDescription, notes, isRemote,
        //  salaryMin, salaryMax, country, usState, city, dateApplied,
        //  datePosted, jobStatus, links, foundFrom
        // - In the Node/Express version, I just edited even the values that
        //   stayed the same for simplicity
        // https://stackoverflow.com/questions/11881479/how-do-i-update-an-entity-using-spring-data-jpa
        // - JPA follows the latter approach. save() in Spring Data JPA is
        //   backed by merge() in plain JPA, therefore it makes your entity
        //   managed as described above. It means that calling save() on an
        //   object with predefined id will update the corresponding database
        //   record rather than insert a new one, and also explains why save()
        //   is not called create()
        // https://stackoverflow.com/questions/74847355/what-is-the-right-way-to-update-a-db-entry-in-spring-jpa
        // a.) Get from DB -> Update using setters -> save
        // b.) Native query
        // ----------------------------
        // Which one to choose?
        // a.) Spring Data JPA way, but requires two database queries
        //     - One to get the job, another one to save
        // b.) Faster since only requires one database query
        //     - However, we want to return the job later, so we'll need to
        //       get it since modifying queries can't return the modified
        //       object
        //     - I'm also getting the job in the first place (see #1), so I'm
        //       already doing the call to get to begin with
        // - I can just return the job from the get, since it'll have all the
        //   updates
        // https://www.baeldung.com/spring-data-partial-update
        // - TODO: (5/13/25) Read this !!!
    }

    @Transactional
    public ResponseJobDto deleteJobByIdAndUserId(UUID id, UUID userId) {
        // 1.) Delete job
        // 2.) Delete resume from database (if any)
        // 3.) Delete cover letter from database (if any)
        // 4.) Delete resume from S3 (if any)
        // 5.) Delete cover letter from S3 (if any)
        // 6.) Return deleted job with files

        // Need to get job first, then delete so we can return the job later
        //   since modifying queries like delete can only return void, int, or
        //   Integer
        ResponseJobDto job = jobRepository.findById(id).map(JobMapper::toResponseDto).orElse(null);
        if (job != null) { // Job exists
            Integer numDeleted = jobRepository.deleteByIdAndMemberId(id, userId);
            if (numDeleted > 0) {
                return job;
            }
        }

        return null; // Job not found or not deleted
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