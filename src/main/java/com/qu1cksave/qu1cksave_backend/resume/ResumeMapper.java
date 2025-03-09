package com.qu1cksave.qu1cksave_backend.resume;

public class ResumeMapper {
    public static ResumeDto toDto(Resume entity) {
        return new ResumeDto(
            entity.getId(),
            entity.getMemberId(),
            entity.getFileName(),
            entity.getMimeType()
        );
    }

    public static Resume toEntity(ResumeDto dto) {
        return new Resume(
            dto.getId(),
            dto.getMemberId(),
            dto.getFileName(),
            dto.getMimeType()
        );
    }
}
