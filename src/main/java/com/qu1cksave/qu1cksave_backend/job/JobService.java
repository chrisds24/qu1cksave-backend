package com.qu1cksave.qu1cksave_backend.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JobService {
    private JobRepository jobRepository;

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
    public Job[] getJobs() {
        // return jobRepository.getJobs(userId); // Use something like this instead
        return new Job[1];
    }

    public Job getJob() {
        // TODO: Fix this later
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

// Setting environment variables:
// - https://unix.stackexchange.com/questions/56444/how-do-i-set-an-environment-variable-on-the-command-line-and-have-it-appear-in-c
// - https://stackoverflow.com/questions/62119161/adding-environment-variables-to-springs-application-properties
//   -- Input "export POSTGRES_HOST=myvalue" in terminal to set environment variables
//   -- Setting it in Intellij doesn't seem to work