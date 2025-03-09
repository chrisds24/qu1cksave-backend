package com.qu1cksave.qu1cksave_backend.coverletter;

public class CoverLetterMapper {
    public static CoverLetterDto toDto(CoverLetter entity) {
        return new CoverLetterDto(
            entity.getId(),
            entity.getMemberId(),
            entity.getFileName(),
            entity.getMimeType()
        );
    }

    public static CoverLetter toEntity(CoverLetterDto dto) {
        return new CoverLetter(
            dto.getId(),
            dto.getMemberId(),
            dto.getFileName(),
            dto.getMimeType()
        );
    }
}
