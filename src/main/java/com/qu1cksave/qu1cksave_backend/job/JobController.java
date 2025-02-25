package com.qu1cksave.qu1cksave_backend.job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
public class JobController {
    // https://stackoverflow.com/questions/63259116/what-is-the-difference-between-using-autowired-annotation-and-private-final
    // - The above is regarding service and repos, but the same idea may apply
    // - Seems like I should use private final
    // https://stackoverflow.com/questions/54118790/how-do-i-properly-inject-many-services-into-spring-mvc-controller
    // - Here, the poster injected multiple services into a controller
    private final JobService jobService;

    public JobController(@Autowired JobService jobService) {
        this.jobService = jobService;
    }

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
        return jobService.getJobs();
    }

    @GetMapping("/{id}")
    public Job getJob(@PathVariable String id) {
        return jobService.getJob();
    }
}
