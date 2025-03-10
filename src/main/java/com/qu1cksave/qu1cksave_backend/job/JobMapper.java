package com.qu1cksave.qu1cksave_backend.job;

import com.qu1cksave.qu1cksave_backend.coverletter.CoverLetterMapper;
import com.qu1cksave.qu1cksave_backend.resume.ResumeMapper;

import java.util.HashMap;
import java.util.Map;

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
            entity.getResume() != null ? ResumeMapper.toDto(entity.getResume()) : null,
            entity.getCoverLetterId(),
            entity.getCoverLetter() != null ? CoverLetterMapper.toDto(entity.getCoverLetter()) : null,
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
            entity.getDateApplied() != null ?
                JobMapper.toYearMonthDate(entity.getDateApplied()) : null,
            entity.getDatePosted() != null ?
                JobMapper.toYearMonthDate(entity.getDatePosted()) : null,
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
            dto.getResume() != null ?
                ResumeMapper.toEntity(dto.getResume()) : null,
            dto.getCoverLetterId(),
            dto.getCoverLetter() != null ?
                CoverLetterMapper.toEntity(dto.getCoverLetter()) : null,
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
            dto.getDateApplied() != null ?
                JobMapper.toMap(dto.getDateApplied()) : null,
            dto.getDatePosted() != null ?
                JobMapper.toMap(dto.getDatePosted()) : null,
            dto.getJobStatus(),
            dto.getLinks(),
            dto.getFoundFrom()
        );
    }

    // Map<String, Object> to YearMonthDate
    public static YearMonthDate toYearMonthDate(Map<String, Object> mapYearMonthDate) {
        return new YearMonthDate(
            Integer.valueOf(String.valueOf(mapYearMonthDate.get("year"))),
            Integer.valueOf(String.valueOf(mapYearMonthDate.get("month"))),
            Integer.valueOf(String.valueOf(mapYearMonthDate.get("date")))
        );
    }

    public static Map<String, Object> toMap(YearMonthDate yearMonthDate) {
        Map<String, Object> mapYearMonthDate = new HashMap<String, Object>();
        mapYearMonthDate.put("year", yearMonthDate.getYear().toString());
        mapYearMonthDate.put("month", yearMonthDate.getMonth().toString());
        mapYearMonthDate.put("date", yearMonthDate.getDate().toString());
        return mapYearMonthDate;
    }
}
