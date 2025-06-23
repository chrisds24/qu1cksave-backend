package com.qu1cksave.qu1cksave_backend.resume;

import com.qu1cksave.qu1cksave_backend.exceptions.SQLGetFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ResumeService {
    private final ResumeRepository resumeRepository;

    public ResumeService(@Autowired ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    @Transactional(readOnly = true)
    public ResponseResumeDto getResume(UUID id, UUID userId) {
        try {
            ResponseResumeDto resume = resumeRepository
                .findByIdAndMemberId(id, userId)
                .map(ResumeMapper::toResponseDto)
                .orElse(null);

            if (resume == null) {
                return null;
            }

            // TODO: Get from S3 and do data conversions
            //   ...

            // Add the actual resume file to the response resume
            //   TODO: This is a temp value
            double[] arr = {2, 4, 7, 10, 14};
            return new ResponseResumeDto(
              resume.getId(),
              resume.getMemberId(),
              resume.getFileName(),
              resume.getMimeType(),
              arr
            );
        } catch (RuntimeException err) {
            throw new SQLGetFailedException("Select one resume failed", err);
        }
    }
}
