package com.qu1cksave.qu1cksave_backend.job;


import com.qu1cksave.qu1cksave_backend.coverletter.CoverLetter;
import com.qu1cksave.qu1cksave_backend.resume.Resume;

import java.util.UUID;

// https://thorben-janssen.com/spring-data-jpa-dto-native-queries/
// - Spring Data JPA native queries with DTO
public interface JobInterfaceProjection {
    UUID getId();
    UUID getMemberId();
    UUID getResumeId();
//    ResumeDto getResume();
    Resume getResume();
    UUID getCoverLetterId();
//    CoverLetterDto getCoverLetter();
    CoverLetter getCoverLetter();
    String getTitle();
    String getCompanyName();
    String getJobDescription();
    String getNotes();
    String getIsRemote();
    Integer getSalaryMin();
    Integer getSalaryMax();
    String getCountry();
    String getUsState();
    String getCity();
    String getDateSaved();
    YearMonthDate getDateApplied();
    YearMonthDate getDatePosted();
    String getJobStatus();
    String[] getLinks();
    String getFoundFrom();
}
