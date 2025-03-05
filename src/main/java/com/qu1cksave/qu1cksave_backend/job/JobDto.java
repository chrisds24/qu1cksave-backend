package com.qu1cksave.qu1cksave_backend.job;

import com.qu1cksave.qu1cksave_backend.coverletter.CoverLetter;
import com.qu1cksave.qu1cksave_backend.resume.Resume;

import java.util.UUID;

public class JobDto {
    // TODO: Make these final
    private UUID id; // Not nullable
    private UUID memberId; // Not nullable
    private UUID resumeId;
//    private Resume resume;
    private UUID coverLetterId;
//    private CoverLetter coverLetter;
    private String title; // Not nullable
    private String companyName; // Not nullable
    private String jobDescription;
    private String notes;
    private String isRemote; // Not nullable
    private int salaryMin; // Integer in entity
    private int salaryMax; // Integer in entity
    private String country;
    private String usState;
    private String city;
    private String dateSaved; // Not nullable
    private YearMonthDate dateApplied;
    private YearMonthDate datePosted;
    private String jobStatus; // Not nullable
    private String[] links;
    private String foundFrom;

    // Constructor
    public JobDto(
        UUID id,
        UUID memberId,
        UUID resumeId,
//        Resume resume,
        UUID coverLetterId,
//        CoverLetter coverLetter,
        String title,
        String companyName,
        String jobDescription,
        String notes,
        String isRemote,
        int salaryMin,
        int salaryMax,
        String country,
        String usState,
        String city,
        String dateSaved,
        YearMonthDate dateApplied,
        YearMonthDate datePosted,
        String jobStatus,
        String[] links,
        String foundFrom
    ) {
        this.id = id;
        this.memberId = memberId;
        this.resumeId = resumeId;
//        this.resume = resume;
        this.coverLetterId = coverLetterId;
//        this.coverLetter = coverLetter;
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
        this.dateSaved = dateSaved;
        this.dateApplied = dateApplied;
        this.datePosted = datePosted;
        this.jobStatus = jobStatus;
        this.links = links;
        this.foundFrom = foundFrom;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getMemberId() { return memberId; }
    public UUID getResumeId() { return resumeId; }
//    public Resume getResume() { return resume; }
    public UUID getCoverLetterId() { return coverLetterId; }
//    public CoverLetter getCoverLetter() { return coverLetter; }
    public String getTitle() { return title; }
    public String getCompanyName() { return companyName; }
    public String getJobDescription() { return jobDescription; }
    public String getNotes() { return notes; }
    public String getIsRemote() { return isRemote; }
    public int getSalaryMin() { return salaryMin; }
    public int getSalaryMax() { return salaryMax; }
    public String getCountry() { return country; }
    public String getUsState() { return usState; }
    public String getCity() { return city; }
    public String getDateSaved() { return dateSaved; }
    public YearMonthDate getDateApplied() { return dateApplied; }
    public YearMonthDate getDatePosted() { return datePosted; }
    public String getJobStatus() { return jobStatus; }
    public String[] getLinks() { return links; }
    public String getFoundFrom() { return foundFrom; }
}
