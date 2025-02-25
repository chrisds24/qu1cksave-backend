package com.qu1cksave.qu1cksave_backend.job;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
public class JobController {
    // TODO: Is having /jobs and a separate /jobs/me (getUserJobs) better?
    // - For example, what if I have an admin role that can access all jobs?
    //   (Though, that is ethically wrong due to privacy reasons)
    //   Then that admin can use getJobs to get all jobs, using query params
    //     to filter those jobs (which is why I'm not using it here to filter
    //     the current user's jobs).
//    @GetMapping("/me")
//    public Job[] getUserJobs(
//        // Need to get id of user from the JWT in the Auth header here
//    ) {
//        // code here
//    }

    @GetMapping()
    public Job[] getJobs() {
        return new Job[1];
    }

    @GetMapping("/{id}")
    public Job getJob(@PathVariable String id) {
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
