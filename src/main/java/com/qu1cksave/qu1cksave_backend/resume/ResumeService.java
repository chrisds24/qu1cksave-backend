package com.qu1cksave.qu1cksave_backend.resume;

import com.qu1cksave.qu1cksave_backend.exceptions.S3GetFailedException;
import com.qu1cksave.qu1cksave_backend.exceptions.SQLGetFailedException;
import com.qu1cksave.qu1cksave_backend.s3.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final S3Service s3Service;

    public ResumeService(
        @Autowired ResumeRepository resumeRepository,
        @Autowired S3Service s3Service
    ) {
        this.resumeRepository = resumeRepository;
        this.s3Service = s3Service;
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

            // Get from S3 and do data conversions
            double[] byteArrayAsArray = null;
            try {
                byteArrayAsArray = s3Service.getObject(resume.getId());
                // This won't be null. If the object doesn't exist, the s3 call
                //   throws an exception
            } catch (RuntimeException err) {
//                System.out.println("Error getting file from S3 with id: " + resume.getId()); // TODO: Comment out later
                throw new S3GetFailedException("Error getting file from S3.");
            }

            if (byteArrayAsArray == null) { // Shouldn't really happen
//                System.out.println("File from S3 with id: " + resume.getId() + " is null"); // TODO: Comment out later
                throw new RuntimeException(
                    "byteArrayAsArray is null even it shouldn't be"
                );
            }

            return new ResponseResumeDto(
                resume.getId(),
                resume.getMemberId(),
                resume.getFileName(),
                resume.getMimeType(),
                byteArrayAsArray
            );
        } catch (RuntimeException err) {
            throw new SQLGetFailedException("Select one resume failed", err);
        }
    }
}
