package com.qu1cksave.qu1cksave_backend.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobService {
//    private JobRepository jobRepository;
//
//    public JobService(@Autowired JobRepository jobRepository) {
//        this.jobRepository = jobRepository;
//    }

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
        return new Job(
            " id",
            "memberId",
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
