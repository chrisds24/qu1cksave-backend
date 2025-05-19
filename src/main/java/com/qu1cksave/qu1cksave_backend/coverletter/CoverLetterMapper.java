package com.qu1cksave.qu1cksave_backend.coverletter;

import java.util.UUID;

public class CoverLetterMapper {
    public static ResponseCoverLetterDto toResponseDto(CoverLetter entity) {
        return new ResponseCoverLetterDto(
            entity.getId(),
            entity.getMemberId(),
            entity.getFileName(),
            entity.getMimeType()
        );
    }

    public static CoverLetter createEntity(RequestCoverLetterDto dto, UUID userId) {
        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setMemberId(userId);
        coverLetter.setFileName(dto.getFileName());
        coverLetter.setMimeType(dto.getMimeType());
        return coverLetter;
    }
}
