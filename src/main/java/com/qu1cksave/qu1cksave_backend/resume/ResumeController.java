package com.qu1cksave.qu1cksave_backend.resume;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/resume")
@Validated
public class ResumeController {
    private final ResumeService resumeService;

    public ResumeController(@Autowired ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseResumeDto> getResume(
        @PathVariable UUID id,
        @RequestAttribute String userId
    ) {
        UUID authUserId = UUID.fromString(userId);
        ResponseResumeDto resume = resumeService.getResume(id, authUserId);
        return new ResponseEntity<ResponseResumeDto>(
            resume, resume != null ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }
}
