package com.qu1cksave.qu1cksave_backend.resume;

public class ResumeMapper {
    public static ResponseResumeDto toDto(Resume entity) {
        return new ResponseResumeDto(
            entity.getId(),
            entity.getMemberId(),
            entity.getFileName(),
            entity.getMimeType()
        );
    }

    public static Resume toEntity(ResponseResumeDto dto) {
        return new Resume(
            dto.getId(),
            dto.getMemberId(),
            dto.getFileName(),
            dto.getMimeType()
        );
    }
}
