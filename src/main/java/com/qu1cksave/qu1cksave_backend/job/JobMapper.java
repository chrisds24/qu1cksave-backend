package com.qu1cksave.qu1cksave_backend.job;

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
