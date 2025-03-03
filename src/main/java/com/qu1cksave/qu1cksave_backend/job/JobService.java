package com.qu1cksave.qu1cksave_backend.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class JobService {
    private final JobRepository jobRepository;

    // If I need property value, Ex:
    //   @Value("${postgres.host}") String postgresHost
    public JobService(@Autowired JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // TODO: Change to use the userId and @Transactional
    // - Note: https://www.marcobehler.com/guides/spring-transaction-management-transactional-in-depth
    //   -- I'll need a HibernateTransactionManager
//    @Transactional
//    public Job[] getJobs(String userId) {
    public List<Job> getJobs() {
        return jobRepository.findAll();
    }

    public Job getJob(UUID id) {
//        return jobRepository.findByUuid(id);
        // Test return value
        return new Job(
            UUID.fromString("1c27e3ee-0307-4e0d-ac0f-dadfcc420ce3"),
            UUID.fromString("1c27e3ee-0307-4e0d-ac0f-dadfcc420ce3"),
            "title",
            "companyName",
            "jobDescription",
            "notes",
            "isRemote",
            1,
            100,
            "country",
            "usState",
            "city",
            "dateSaved",
            new YearMonthDate(2025,3, 2),
            new YearMonthDate(2025, 3, 2),
            "jobStatus",
            new String[1],
            "foundFrom"
        );
    }
}