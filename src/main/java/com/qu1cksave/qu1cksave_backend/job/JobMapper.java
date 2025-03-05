package com.qu1cksave.qu1cksave_backend.job;

import java.util.UUID;

public class JobMapper {
    public static JobDto toDto(Job entity) {
        // TODO: Search about:
        //   - "nullable fields DTO"
        //   - "multiple constructors DTO"
        //      -- Or instead: "optional parameters constructor"
        return new JobDto(
            entity.getId(),
            entity.getMemberId(),
            entity.getResumeId(),
            entity.getCoverLetterId(),
            entity.getTitle(),
            entity.getCompanyName(),
            entity.getJobDescription(),
            entity.getNotes(),
            entity.getIsRemote(),
            entity.getSalaryMin(),
            entity.getSalaryMax(),
            entity.getCountry(),
            entity.getUsState(),
            entity.getCity(),
            entity.getDateSaved(),
            entity.getDateApplied(),
            entity.getDatePosted(),
            entity.getJobStatus(),
            entity.getLinks(),
            entity.getFoundFrom()
        );
    }

    public static Job toEntity(JobDto dto) {
        return new Job(
            dto.getId(),
            dto.getMemberId(),
            dto.getResumeId(),
            dto.getCoverLetterId(),
            dto.getTitle(),
            dto.getCompanyName(),
            dto.getJobDescription(),
            dto.getNotes(),
            dto.getIsRemote(),
            dto.getSalaryMin(),
            dto.getSalaryMax(),
            dto.getCountry(),
            dto.getUsState(),
            dto.getCity(),
            dto.getDateSaved(),
            dto.getDateApplied(),
            dto.getDatePosted(),
            dto.getJobStatus(),
            dto.getLinks(),
            dto.getFoundFrom()
        );
    }
}

// USEFUL when we have a list of another entity that we need to convert
//   to its DTO form. I won't need this since I can just call
//   ResumeDto.toDto(entity.getResume())
//        entity.getBooks().stream().map(UserMapper::toDto).collect(Collectors.toList())
