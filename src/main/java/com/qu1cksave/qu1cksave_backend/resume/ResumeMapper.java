package com.qu1cksave.qu1cksave_backend.resume;

import java.util.UUID;

public class ResumeMapper {
    public static ResponseResumeDto toResponseDto(Resume entity) {
        return new ResponseResumeDto(
            entity.getId(),
            entity.getMemberId(),
            entity.getFileName(),
            entity.getMimeType(),
            null // Entity has no byteArrayAsArray
        );
    }

    public static Resume createEntity(RequestResumeDto dto, UUID userId) {
        Resume resume = new Resume();
        resume.setMemberId(userId);
        resume.setFileName(dto.getFileName());
        resume.setMimeType(dto.getMimeType());
        return resume;
    }
}
