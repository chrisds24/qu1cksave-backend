package com.qu1cksave.qu1cksave_backend.coverletter;

import com.qu1cksave.qu1cksave_backend.exceptions.SQLGetFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CoverLetterService {
    private final CoverLetterRepository coverLetterRepository;

    public CoverLetterService(@Autowired CoverLetterRepository coverLetterRepository) {
        this.coverLetterRepository = coverLetterRepository;
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

            // TODO: Get from S3 and do data conversions
            //   ...

            // Add the actual coverLetter file to the response coverLetter
            //   TODO: This is a temp value
            double[] arr = {2, 4, 7, 10, 14};
            return new ResponseCoverLetterDto(
                coverLetter.getId(),
                coverLetter.getMemberId(),
                coverLetter.getFileName(),
                coverLetter.getMimeType(),
                arr
            );
        } catch (RuntimeException err) {
            throw new SQLGetFailedException("Select one cover letter failed", err);
        }
    }
}
