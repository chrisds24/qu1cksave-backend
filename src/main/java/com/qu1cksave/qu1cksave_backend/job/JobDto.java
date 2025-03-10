package com.qu1cksave.qu1cksave_backend.job;

import com.qu1cksave.qu1cksave_backend.coverletter.CoverLetter;
import com.qu1cksave.qu1cksave_backend.coverletter.CoverLetterDto;
import com.qu1cksave.qu1cksave_backend.resume.ResumeDto;
import org.springframework.data.annotation.PersistenceCreator;

import java.util.UUID;

public class JobDto {
    // TODO: How to specify NOT nullable?
    // TODO: Search for:
    // - multiple constructors DTO
    // - required/nullable parameters constructor DTO
    // How to specify nullable?
    // - Optional could work to specify nullable...But there's a better way
    // - https://stackoverflow.com/questions/70065782/how-to-make-a-field-optional
    //   -- In Java, all (non-primitive) types are nullable, hence can be
    //      seen optional. So you could just assign it null and call it a day
    // https://stackoverflow.com/questions/7504064/does-java-allow-nullable-types
    // - No, in java primitives cannot have null value, if you want this
    //   feature, you might want to use Boolean instead.
    // https://stackoverflow.com/questions/58182553/notnull-annotation-is-not-working-in-spring-boot-application
    // Optional:
    // - https://stackoverflow.com/questions/23454952/uses-for-optional
    //   -- Keep seeing that Optional is bad practice for fields
    // - https://www.baeldung.com/java-optional
    // TODO: https://stackoverflow.com/questions/1281952/what-is-the-easiest-way-to-ignore-a-jpa-field-during-persistence
    //  - You can also use JsonInclude.Include.NON_NULL and hide fields in JSON
    //    during deserialization
    //  - @JsonInclude(JsonInclude.Include.NON_NULL)
    //  - ME: I can use this if I want to not return null fields as part of the
    //    JSON response

    private final UUID id; // NOT NULLABLE                          // 1
    private final UUID memberId; // NOT NULLABLE                    // 2
    private final UUID resumeId;                                    // 3
    private final ResumeDto resume;                                 // 4
    private final UUID coverLetterId;                               // 5
    private final CoverLetterDto coverLetter;                       // 6
    private final String title; // NOT NULLABLE                     // 7
    private final String companyName; // NOT NULLABLE               // 8
    private final String jobDescription;                            // 9
    private final String notes;                                     // 10
    private final String isRemote; // NOT NULLABLE                  // 11
    private final Integer salaryMin;                                // 12
    private final Integer salaryMax;                                // 13
    private final String country;                                   // 14
    private final String usState;                                   // 15
    private final String city;                                      // 16
    private final String dateSaved; // NOT NULLABLE                 // 17
    private final YearMonthDate dateApplied;                        // 18
    private final YearMonthDate datePosted;                         // 19
    private final String jobStatus; // NOT NULLABLE                 // 20
    private final String[] links;                                   // 21
    private final String foundFrom;                                 // 22

    // Constructor
    // https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html#projections.dtos
    // - When using class-based projections and there's more than one
    //   constructor, need further hints for DTO projections such as
    //   @PersistenceCreator
    @PersistenceCreator
    public JobDto(
        UUID id,
        UUID memberId,
        UUID resumeId,
        ResumeDto resume,
        UUID coverLetterId,
        CoverLetterDto coverLetter,
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
    public ResumeDto getResume() { return resume; }
    public UUID getCoverLetterId() { return coverLetterId; }
    public CoverLetterDto getCoverLetter() { return coverLetter; }
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
    public String getDateSaved() { return dateSaved; }
    public YearMonthDate getDateApplied() { return dateApplied; }
    public YearMonthDate getDatePosted() { return datePosted; }
    public String getJobStatus() { return jobStatus; }
    public String[] getLinks() { return links; }
    public String getFoundFrom() { return foundFrom; }
}
