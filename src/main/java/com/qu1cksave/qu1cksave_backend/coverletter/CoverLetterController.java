package com.qu1cksave.qu1cksave_backend.coverletter;

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
@RequestMapping("/coverLetter")
@Validated
public class CoverLetterController {
    private final CoverLetterService coverLetterService;

    public CoverLetterController(@Autowired CoverLetterService coverLetterService) {
        this.coverLetterService = coverLetterService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseCoverLetterDto> getCoverLetter(
        @PathVariable UUID id,
        @RequestAttribute String userId
    ) {
        UUID authUserId = UUID.fromString(userId);
        ResponseCoverLetterDto coverLetter = coverLetterService.getCoverLetter(id, authUserId);
        return new ResponseEntity<ResponseCoverLetterDto>(
            coverLetter, coverLetter != null ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }
}
