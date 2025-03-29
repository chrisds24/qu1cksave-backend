package com.qu1cksave.qu1cksave_backend.coverletter;

public class CoverLetterMapper {
    public static ResponseCoverLetterDto toDto(CoverLetter entity) {
        return new ResponseCoverLetterDto(
            entity.getId(),
            entity.getMemberId(),
            entity.getFileName(),
            entity.getMimeType()
        );
    }

    public static CoverLetter toEntity(ResponseCoverLetterDto dto) {
        return new CoverLetter(
            dto.getId(),
            dto.getMemberId(),
            dto.getFileName(),
            dto.getMimeType()
        );
    }
}
