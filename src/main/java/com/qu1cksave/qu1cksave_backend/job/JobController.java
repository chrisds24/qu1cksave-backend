package com.qu1cksave.qu1cksave_backend.job;
import com.qu1cksave.qu1cksave_backend.exceptions.ForbiddenResourceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
//@RequestMapping("/jobs")
@RequestMapping("/job")
// https://medium.com/@tericcabrel/validate-request-body-and-parameter-in-spring-boot-53ca77f97fe9
// - @Validated on controller
@Validated
public class JobController {
    // https://stackoverflow.com/questions/63259116/what-is-the-difference-between-using-autowired-annotation-and-private-final
    // - The above is regarding service and repos, but the same idea may apply
    private final JobService jobService;
    // Autowiring the request is not commonly done for controllers
//    private HttpServletRequest req;

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

    // TODO: (5/7/25): I need to set the URI for my whole backend
    //  Ex. /api/v1     instead of just /
    //  So it would become http://localhost:8080/api/v1/job?id=269a3d55-4eee-4a2e-8c64-e1fe386b76f8
    //  Instead of http://localhost:8080/job?id=269a3d55-4eee-4a2e-8c64-e1fe386b76f8 (CURRENT)

    // TODO: For everything below, need to get id of user from the JWT in the
    //   Auth header. I remember it's passed as a parameter in Spring Security
    //   Also need OpenAPI schema validation (or something similar)

    @GetMapping()
    // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RequestParam.html
    // - RequestParams are required by default
    // https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-methods/requestattrib.html
    // - To get request attributes, just use @RequestAttribute instead of
    //   using HttpServletRequest
    public List<ResponseJobDto> getJobs(
        @RequestParam("id") UUID queryUserId,
        @RequestAttribute String userId
    ) {
        // UPDATE: When no queryUserId is provided, a
        //   MissingServletRequestParameterException is thrown

        UUID authUserId = UUID.fromString(userId);

        // User wants jobs that don't belong to them, so return an error
        if (!authUserId.equals(queryUserId)) {
            throw new ForbiddenResourceException(
                "Mismatch between auth header and query param user id"
            );
        }

        return jobService.getJobs(authUserId);

        // For testing: http://localhost:8080/jobs?id=269a3d55-4eee-4a2e-8c64-e1fe386b76f8
    }

    // https://stackoverflow.com/questions/25422255/how-to-return-404-response-status-in-spring-boot-responsebody-method-return-t
    // - ResponseEntity to set status code
    // https://stackoverflow.com/questions/24292373/spring-boot-rest-controller-how-to-return-different-http-status-codes
    // - Pass HttpServletResponse to controller to set status code
    // https://stackoverflow.com/questions/56008051/difference-between-httpservletresponse-and-a-responseentityspring
    // - HttpServletResponse is from Java. ResponseEntity is from Spring
    // NOTE: getJob is only used for testing.
    @GetMapping("/{id}") // NOTE: For now, this is used for testing only
    public ResponseEntity<ResponseJobDto> getJob(
        @PathVariable UUID id,
        @RequestAttribute String userId
    ) {
        UUID authUserId = UUID.fromString(userId);
        ResponseJobDto job = jobService.getJob(id, authUserId);
        return new ResponseEntity<ResponseJobDto>(
            job, job != null ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    // https://www.baeldung.com/spring-boot-bean-validation
    // - @Valid on request body
    public ResponseJobDto createJob(
        @Valid @RequestBody RequestJobDto newJob,
        @RequestAttribute String userId
    ) {
        UUID authUserId = UUID.fromString(userId);
        return jobService.createJob(newJob, authUserId);
    }

    @PutMapping("/{id}")
    public ResponseJobDto editJob(
        @PathVariable UUID id,
        @Valid @RequestBody RequestJobDto editJob,
        @RequestAttribute String userId
    ) {
        UUID authUserId = UUID.fromString(userId);
//        ResponseJobDto job = jobService.editJob(id, authUserId, editJob);
//        return new ResponseEntity<ResponseJobDto>(job, job != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
        return jobService.editJob(id, authUserId, editJob);
    }

    @DeleteMapping("/{id}")
    // 200 for delete if returning something
    public ResponseJobDto deleteJob(
        @PathVariable UUID id,
        @RequestAttribute String userId
    ) {
        UUID authUserId = UUID.fromString(userId);
//        ResponseJobDto job = jobService.deleteJob(id, authUserId);
//        return new ResponseEntity<ResponseJobDto>(job, job != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
        return jobService.deleteJob(id, authUserId);
    }
}
