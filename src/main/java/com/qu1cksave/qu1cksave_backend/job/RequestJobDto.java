package com.qu1cksave.qu1cksave_backend.job;

import com.qu1cksave.qu1cksave_backend.coverletter.RequestCoverLetterDto;
import com.qu1cksave.qu1cksave_backend.resume.RequestResumeDto;

import java.util.Map;
import java.util.UUID;

public class RequestJobDto {
    // TODO: (3/29/25) How to specify which fields can be omitted?
    private final UUID resumeId;
    private final RequestResumeDto resume;
    private final UUID coverLetterId;
    private final RequestCoverLetterDto coverLetter;
    private final String title; // NOT NULLABLE
    private final String companyName; // NOT NULLABLE
    private final String jobDescription;
    private final String notes;
    private final String isRemote; // NOT NULLABLE
    private final Integer salaryMin;
    private final Integer salaryMax;
    private final String country;
    private final String usState;
    private final String city;
    private final Map<String, Object> dateApplied;
    private final Map<String, Object> datePosted;
    private final String jobStatus; // NOT NULLABLE
    private final String[] links;
    private final String foundFrom;
    // In job EDIT mode, used to determine if resume is to be deleted or not
    private final boolean keepResume;
    private final boolean keepCoverLetter;

    // Constructors
    public RequestJobDto(
        UUID resumeId,
        RequestResumeDto resume,
        UUID coverLetterId,
        RequestCoverLetterDto coverLetter,
        String title,
        String companyName,
        String jobDescription,
        String notes,
        String isRemote,
        Integer salaryMin,
        Integer salaryMax,
        String country,
        String usState,
        String city,
        Map<String, Object> dateApplied,
        Map<String, Object> datePosted,
        String jobStatus,
        String[] links,
        String foundFrom,
        boolean keepResume, // TODO: (3/29/25) Should I use Boolean instead?
        boolean keepCoverLetter
    ) {
        this.resumeId = resumeId;
        this.resume = resume;
        this.coverLetterId = coverLetterId;
        this.coverLetter = coverLetter;
        this.title = title;
        this.companyName = companyName;
        this.jobDescription = jobDescription;
        this.notes = notes;
        this.isRemote = isRemote;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.country = country;
        this.usState = usState;
        this.city = city;
        this.dateApplied = dateApplied;
        this.datePosted = datePosted;
        this.jobStatus = jobStatus;
        this.links = links;
        this.foundFrom = foundFrom;
        this.keepResume = keepResume;
        this.keepCoverLetter = keepCoverLetter;
    }

    // Getters
    public UUID getResumeId() { return resumeId; }
    public RequestResumeDto getResume() { return resume; }
    public UUID getCoverLetterId() { return coverLetterId; }
    public RequestCoverLetterDto getCoverLetter() { return coverLetter; }
    public String getTitle() { return title; }
    public String getCompanyName() { return companyName; }
    public String getJobDescription() { return jobDescription; }
    public String getNotes() { return notes; }
    public String getIsRemote() { return isRemote; }
    public Integer getSalaryMin() { return salaryMin; }
    public Integer getSalaryMax() { return salaryMax; }
    public String getCountry() { return country; }
    public String getUsState() { return usState; }
    public String getCity() { return city; }
    public Map<String, Object> getDateApplied() { return dateApplied; }
    public Map<String, Object> getDatePosted() { return datePosted; }
    public String getJobStatus() { return jobStatus; }
    public String[] getLinks() { return links; }
    public String getFoundFrom() { return foundFrom; }
    public boolean getKeepResume() { return keepResume; }
    public boolean getKeepCoverLetter() { return keepCoverLetter; }
}
