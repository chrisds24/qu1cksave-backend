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

    public ResponseJobDto createJob(RequestJobDto newJob, UUID userId) {
        return null; // TODO
    }
}

// Convert string to UUID
//   UUID.fromString("1d27e3ee-1111-4e0d-ac0f-dadfcc420ce3"),

// Keep for reference
//@Transactional(readOnly = true)
//public JobDto getJob(UUID id, UUID userId) {
////        Optional<Job> job = jobRepository.findById(id);
////        return job.isPresent() ? JobMapper.toDto(job.get()) : null;
//    // Suggested functional style by Intellij
//    // https://stackoverflow.com/questions/3907394/java-is-there-a-map-function
//    // - map and orElse are from Optional
//    return jobRepository.findByIdAndMemberId(id, userId)
//        .map(JobMapper::toDto).orElse(null);
//}
