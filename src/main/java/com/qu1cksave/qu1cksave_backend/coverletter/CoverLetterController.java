package com.qu1cksave.qu1cksave_backend.coverletter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        @PathVariable UUID id
    ) {
        UUID authUserId = UUID.fromString("269a3d55-4eee-4a2e-8c64-e1fe386b76f8");
        ResponseCoverLetterDto coverLetter = coverLetterService.getCoverLetter(id, authUserId);
        return new ResponseEntity<ResponseCoverLetterDto>(
            coverLetter, coverLetter != null ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }
}
