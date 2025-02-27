package com.qu1cksave.qu1cksave_backend.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class JobService {
//    private JobRepository jobRepository;
//
//    public JobService(@Autowired JobRepository jobRepository) {
//        this.jobRepository = jobRepository;
//    }
    private final String postgresHost;
    // TODO: Remove later
    private final String fakeDataSource;

    // https://unix.stackexchange.com/questions/56444/how-do-i-set-an-environment-variable-on-the-command-line-and-have-it-appear-in-c
    // https://stackoverflow.com/questions/62119161/adding-environment-variables-to-springs-application-properties
    // Input "export POSTGRES_HOST=myvalue" in terminal to set environment variables
    // - Setting it in Intellij doesn't seem to work
    public JobService(
        @Value("${postgres.host}") String postgresHost,
        @Autowired String fakeDataSource
    ) {
        this.postgresHost = postgresHost;
        this.fakeDataSource = fakeDataSource;
    }

    // TODO: Change to use the userId and @Transactional
    // - Note: https://www.marcobehler.com/guides/spring-transaction-management-transactional-in-depth
    //   -- I might need a PlatformTransactionManager
//    @Transactional
//    public Job[] getJobs(String userId) {
    public Job[] getJobs() {
        // return jobRepository.getJobs(userId); // Use something like this instead
        return new Job[1];
    }

    public Job getJob() {
        // TODO: Fix this later
        return new Job(
            postgresHost,
            fakeDataSource,
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
            "jobStatus",
            new String[1],
            "foundFrom"
        );
    }
}
