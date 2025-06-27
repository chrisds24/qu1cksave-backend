package com.qu1cksave.qu1cksave_backend.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qu1cksave.qu1cksave_backend.coverletter.RequestCoverLetterDto;
import com.qu1cksave.qu1cksave_backend.resume.RequestResumeDto;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

// Used for add new job and edit job
public class RequestJobDto {
    // NOTE:
    //  - (6/16/25) I haven't added @NotNull constraints to the response
    //    dtos. The entities have @Column(...nullable=false)
    private final UUID resumeId;
    private final RequestResumeDto resume;
    private final UUID coverLetterId;
    private final RequestCoverLetterDto coverLetter;
    @NotNull
    private final String title; // NOT NULLABLE
    @NotNull
    private final String companyName; // NOT NULLABLE
    private final String jobDescription;
    private final String notes;
    @NotNull
    private final String isRemote; // NOT NULLABLE
    private final Integer salaryMin;
    private final Integer salaryMax;
    private final String country;
    private final String usState;
    private final String city;
    private final YearMonthDateDto dateApplied;
    private final YearMonthDateDto datePosted;
    @NotNull
    private final String jobStatus; // NOT NULLABLE
    private final String[] links;
    private final String foundFrom;
    // In job EDIT mode, used to determine if resume is to be deleted or not
    // - I'm using Boolean (object) instead of boolean(primitive) since these
    //   fields could be null
    private final Boolean keepResume;
    private final Boolean keepCoverLetter;

    // Constructors
    public RequestJobDto(
        @JsonProperty("resume_id") UUID resumeId,
        @JsonProperty("resume") RequestResumeDto resume,
        // TODO: (3/30/25) For now, I'm using the actual type instead of String
        //   since I want to check if Jackson is able to convert
        //   UPDATE: (5/11/25) Jackson is able to convert String[] links
        //    and also dateApplied/Posted
//        String Resume, // In case Jackson can't automatically convert
        @JsonProperty("cover_letter_id") UUID coverLetterId,
        @JsonProperty("cover_letter") RequestCoverLetterDto coverLetter,
//        String CoverLetter,
        @JsonProperty("title") String title,
        @JsonProperty("company_name") String companyName,
        @JsonProperty("job_description") String jobDescription,
        @JsonProperty("notes") String notes,
        @JsonProperty("is_remote") String isRemote,
        @JsonProperty("salary_min") Integer salaryMin,
        @JsonProperty("salary_max") Integer salaryMax,
        @JsonProperty("country") String country,
        @JsonProperty("us_state") String usState,
        @JsonProperty("city") String city,
        @JsonProperty("date_applied") YearMonthDateDto dateApplied,
        @JsonProperty("date_posted") YearMonthDateDto datePosted,
//        String dateApplied,
//        String datePosted,
        @JsonProperty("job_status") String jobStatus,
        @JsonProperty("links") String[] links,
//        String links,
        @JsonProperty("found_from") String foundFrom,
        @JsonProperty("keep_resume") Boolean keepResume,
        @JsonProperty("keep_cover_letter") Boolean keepCoverLetter
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
    public YearMonthDateDto getDateApplied() { return dateApplied; }
    public YearMonthDateDto getDatePosted() { return datePosted; }
    public String getJobStatus() { return jobStatus; }
    public String[] getLinks() { return links; }
    public String getFoundFrom() { return foundFrom; }
    public Boolean getKeepResume() { return keepResume; }
    public Boolean getKeepCoverLetter() { return keepCoverLetter; }
}
