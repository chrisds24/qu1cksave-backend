package com.qu1cksave.qu1cksave_backend.coverletter;

import com.qu1cksave.qu1cksave_backend.exceptions.S3GetFailedException;
import com.qu1cksave.qu1cksave_backend.exceptions.SQLGetFailedException;
import com.qu1cksave.qu1cksave_backend.s3.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CoverLetterService {
    private final CoverLetterRepository coverLetterRepository;
    private final S3Service s3Service;

    public CoverLetterService(
        @Autowired CoverLetterRepository coverLetterRepository,
        @Autowired S3Service s3Service
    ) {
        this.coverLetterRepository = coverLetterRepository;
        this.s3Service = s3Service;
    }

    @Transactional(readOnly = true)
    public ResponseCoverLetterDto getCoverLetter(UUID id, UUID userId) {
        try {
            ResponseCoverLetterDto coverLetter = coverLetterRepository
                .findByIdAndMemberId(id, userId)
                .map(CoverLetterMapper::toResponseDto)
                .orElse(null);

            if (coverLetter == null) {
                return null;
            }

            // Get from S3 and do data conversions
            double[] byteArrayAsArray = null;
            try {
                byteArrayAsArray = s3Service.getObject(coverLetter.getId());
                // This won't be null. If the object doesn't exist, the s3 call
                //   throws an exception
            } catch (RuntimeException err) {
//                System.out.println("Error getting file from S3 with id: " + coverLetter.getId()); // TODO: Comment out later
                throw new S3GetFailedException("Error getting file from S3.");
            }

            if (byteArrayAsArray == null) { // Shouldn't really happen
//                System.out.println("File from S3 with id: " + coverLetter.getId() + " is null"); // TODO: Comment out later
                throw new RuntimeException(
                    "byteArrayAsArray is null even it shouldn't be"
                );
            }

            return new ResponseCoverLetterDto(
                coverLetter.getId(),
                coverLetter.getMemberId(),
                coverLetter.getFileName(),
                coverLetter.getMimeType(),
                byteArrayAsArray
            );
        } catch (RuntimeException err) {
            throw new SQLGetFailedException("Select one cover letter failed", err);
        }
    }
}
