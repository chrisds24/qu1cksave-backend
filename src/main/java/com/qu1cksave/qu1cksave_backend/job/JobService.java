package com.qu1cksave.qu1cksave_backend.job;

import com.qu1cksave.qu1cksave_backend.coverletter.CoverLetter;
import com.qu1cksave.qu1cksave_backend.resume.Resume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JobService {
    private final JobRepository jobRepository;
    // When I autowire this, it says:
    //   Could not autowire. No beans of 'JobMapper' type found.
    // Since I obviously haven't declared JobMapper as a bean.
//    private final JobMapper jobMapper;

    // If I need a property value, Ex:
    //   @Value("${postgres.host}") String postgresHost
    public JobService(@Autowired JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // TODO: Change to use the userId and @Transactional
    // - Note: https://www.marcobehler.com/guides/spring-transaction-management-transactional-in-depth
    //   -- I'll need a JpaTransactionManager
    // https://stackoverflow.com/questions/10394857/how-to-use-transactional-with-spring-data
    // - Talks about @Transactional(readOnly = true) for select queries
//    @Transactional
//    public Job[] getJobs(String userId) { // USE THIS LATER
    public List<JobDto> getJobs(UUID userId) {
//        return jobRepository.findAll().stream().map(JobMapper::toDto).collect(Collectors.toList());
        return jobRepository.findByMemberId(userId).stream().map(JobMapper::toDto).collect(Collectors.toList());
    }

    public JobDto getJob(UUID id, UUID userId) {
//        Optional<Job> job = jobRepository.findById(id);
//        return job.isPresent() ? JobMapper.toDto(job.get()) : null;
        // Suggested functional style by Intellij
        // https://stackoverflow.com/questions/3907394/java-is-there-a-map-function
        // map and orElse are from Optional
//        return jobRepository.findById(id).map(JobMapper::toDto).orElse(null);
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