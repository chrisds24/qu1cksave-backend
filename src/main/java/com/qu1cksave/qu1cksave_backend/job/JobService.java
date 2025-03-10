package com.qu1cksave.qu1cksave_backend.job;

import com.qu1cksave.qu1cksave_backend.coverletter.CoverLetter;
import com.qu1cksave.qu1cksave_backend.resume.Resume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
    public List<JobDto> getJobs(UUID userId) {
        // TODO: Replace this with a native query or HQL to do a join and get
        //   Resumes/Cover Letters in one query efficiently
        return jobRepository.findByMemberIdWithFiles(userId).stream().map(JobMapper::toDto).collect(Collectors.toList());
//        return jobRepository.findByMemberIdWithFiles(userId);
    }

    @Transactional(readOnly = true)
    public JobDto getJob(UUID id, UUID userId) {
//        Optional<Job> job = jobRepository.findById(id);
//        return job.isPresent() ? JobMapper.toDto(job.get()) : null;
        // Suggested functional style by Intellij
        // https://stackoverflow.com/questions/3907394/java-is-there-a-map-function
        // map and orElse are from Optional
        return jobRepository.findByIdAndMemberId(id, userId)
            .map(JobMapper::toDto).orElse(null);
    }
}

// Test return value for get single job;
//        return JobMapper.toDto(new Job(
//            UUID.fromString("1d27e3ee-1111-4e0d-ac0f-dadfcc420ce3"),
//            UUID.fromString("1c1de3ee-2222-4e0d-ac0f-dadfcc420ce3"),
//            UUID.fromString("1c271dee-3333-4e0d-ac0f-dadfcc420ce3"),
//            new Resume(
//                UUID.fromString("1c271dee-3333-4e0d-ac0f-dadfcc420ce3"),
//                UUID.fromString("1c1de3ee-2222-4e0d-ac0f-dadfcc420ce3"),
//                "ChristianDelosSantos_Resume_SWE_Google",
//                "pdf"
//            ),
//            UUID.fromString("1c27e31d-4444-4e0d-ac0f-dadfcc420ce3"),
//            new CoverLetter(
//                UUID.fromString("1c27e31d-4444-4e0d-ac0f-dadfcc420ce3"),
//                UUID.fromString("1c1de3ee-2222-4e0d-ac0f-dadfcc420ce3"),
//                "ChristianDelosSantos_CoverLetter_SWE_Google",
//                "docx"
//            ),
//            "title",
//            "companyName",
//            "jobDescription",
//            "notes",
//            "isRemote",
//            1,
//            100,
//            "country",
//            "usState",
//            "city",
//            "dateSaved",
//            new YearMonthDate(2025,3, 2),
//            new YearMonthDate(2025, 3, 2),
//            "jobStatus",
//            new String[1],
//            "foundFrom"
//        ));