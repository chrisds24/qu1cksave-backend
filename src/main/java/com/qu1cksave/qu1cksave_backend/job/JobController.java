package com.qu1cksave.qu1cksave_backend.job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/jobs")
public class JobController {
    // https://stackoverflow.com/questions/63259116/what-is-the-difference-between-using-autowired-annotation-and-private-final
    // - The above is regarding service and repos, but the same idea may apply
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
//    public Job[] getUserJobs(...) { ... }

    // TODO: For everything below, need to get id of user from the JWT in the
    //   Auth header. I remember it's passed as a parameter in Spring Security

    @GetMapping()
    public List<ResponseJobDto> getJobs(@RequestParam("id") String userId) {
        // TODO: Replace mollyMemberId with user id obtained from auth header
        //  - I'll compare the one from the query and the auth header
        String strAuthUserId = "269a3d55-4eee-4a2e-8c64-e1fe386b76f8";

        // User wants jobs that don't belong to them, so return an error
        if (!strAuthUserId.equals(userId)) {
            return null; // TODO: Return an appropriate JSON error object
        }

        UUID authUserId = UUID.fromString(strAuthUserId);
        return jobService.getJobs(authUserId);

        // For testing: http://localhost:8080/jobs?id=269a3d55-4eee-4a2e-8c64-e1fe386b76f8
    }

    @PostMapping()
    public ResponseJobDto createJob(@RequestBody RequestJobDto newJob) {
        UUID authUserId = UUID.fromString("269a3d55-4eee-4a2e-8c64-e1fe386b76f8");
        return jobService.createJob(newJob, authUserId);
    }
}
