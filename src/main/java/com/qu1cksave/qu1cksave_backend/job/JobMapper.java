package com.qu1cksave.qu1cksave_backend.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;

// Note:
// https://www.baeldung.com/java-entity-vs-dto
// - This link doesn't have the mapper as a Spring Bean (@Component)
//   So the mapper is used as JobMapper.toDto(jobEntity)
// https://www.baeldung.com/java-dto-pattern
// - Meanwhile, this one has it as a bean
// - It needs to be autowired and used as: jobMapper.toDto(jobEntity)
//   Where jobMapper is the autowired instance set in the constructor
public class JobMapper {
    public static JobDto toDto(Job entity) {
        // https://stackoverflow.com/questions/2015071/why-boolean-in-java-takes-only-true-or-false-why-not-1-or-0-also
        // - Java, unlike languages like C and C++, treats boolean as a
        //   completely separate data type which has 2 distinct values: true
        //   and false. The values 1 and 0 are of type int and are not
        //   implicitly convertible to boolean
        // - ME: So we need to do entity.getSalaryMin() != null for null check
        //   instead of using the value's truthiness/falsiness
        try {
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
                entity.getDateApplied() != null ? new ObjectMapper().writeValueAsString(entity.getDateApplied()) : null,
                entity.getDatePosted() != null ? new ObjectMapper().writeValueAsString(entity.getDatePosted()) : null,
                entity.getJobStatus(),
                // Need to convert String[] to String, since JobDto requires a
                //   String. I couldn't use multiple constructors because JPA
                //   wants JobDto to only have one constructor if I'm using it
                //   as a return type for a method in the repository
                entity.getLinks() != null ? new ObjectMapper().writeValueAsString(entity.getLinks()) : null,
                entity.getFoundFrom(),
                null, // Job entity does not have a resume
                null // Job entity does not have a coverLetter
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
            Instant.parse(dto.getDateSaved()),
            dto.getDateApplied(),
            dto.getDatePosted(),
            dto.getJobStatus(),
            dto.getLinks(),
            dto.getFoundFrom()
            // Job entity doesn't have a resume/cover letter
        );
    }
}
