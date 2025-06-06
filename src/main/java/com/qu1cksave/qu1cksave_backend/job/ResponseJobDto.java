package com.qu1cksave.qu1cksave_backend.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qu1cksave.qu1cksave_backend.coverletter.ResponseCoverLetterDto;
import com.qu1cksave.qu1cksave_backend.resume.ResponseResumeDto;

import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ResponseJobDto {
    // TODO: Search for:
    //  - How to specify not nullable?
    //  - required/nullable parameters constructor DTO
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
    // https://stackoverflow.com/questions/1281952/what-is-the-easiest-way-to-ignore-a-jpa-field-during-persistence
    //  - You can also use JsonInclude.Include.NON_NULL and hide fields in JSON
    //    during deserialization
    //  - @JsonInclude(JsonInclude.Include.NON_NULL)
    //  - ME: I can use this if I want to not return null fields as part of the
    //    JSON response

    private final UUID id; // NOT NULLABLE                          // 1
    private final UUID memberId; // NOT NULLABLE                    // 2
    private final UUID resumeId;                                    // 3
    private final UUID coverLetterId;                               // 5
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
//    private final Map<String, Object> dateApplied;                        // 18
//    private final Map<String, Object> datePosted;                         // 19
    private final YearMonthDateDto dateApplied;                        // 18
    private final YearMonthDateDto datePosted;                         // 19
    private final String jobStatus; // NOT NULLABLE                 // 20
    private final String[] links;                                   // 21
    private final String foundFrom;                                 // 22
    private final ResponseResumeDto resume;                                 // 4
    private final ResponseCoverLetterDto coverLetter;                       // 6

    // Constructors
    // https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html#projections.dtos
    // - When using class-based projections and there's more than one
    //   constructor, need further hints for DTO projections such as
    //   @PersistenceCreator
    // - I didn't end up needing to use projections
    public ResponseJobDto(
        @JsonProperty("id") UUID id,
        @JsonProperty("member_id") UUID memberId,
        @JsonProperty("resume_id") UUID resumeId,
//        ResumeDto resume,
        @JsonProperty("cover_letter_id") UUID coverLetterId,
//        CoverLetterDto coverLetter,
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
        @JsonProperty("date_saved") Instant dateSaved,
        // IMPORTANT: dateApplied and datePosted are originally String here,
        //   but needed to change to object to get rid of the error below when
        //   testing:
        // com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot deserialize value of type `java.lang.String` from Object value (token `JsonToken.START_OBJECT`)
        //         at [Source: UNKNOWN; line: 1, column: 463] (through reference chain: com.qu1cksave.qu1cksave_backend.job.ResponseJobDto["date_posted"])
        //
//        @JsonProperty("date_applied") String dateApplied,
        @JsonProperty("date_applied") Object dateApplied,
        @JsonProperty("date_posted") String datePosted,
        @JsonProperty("job_status") String jobStatus,
// Needs to be a string since no automatic conversion from JSON array
//   to a String array
//        String[] links,
        @JsonProperty("links") String links,
        @JsonProperty("found_from") String foundFrom,
        // This doesn't automatically convert to ResponseResumeDto since it
        //   can't cast String to this
//        @JsonProperty("resume") ResponseResumeDto resume,
//        @JsonProperty("cover_letter") ResponseCoverLetterDto coverLetter
        @JsonProperty("resume") String resume,
        @JsonProperty("cover_letter") String coverLetter
    ) {
        try {
            this.id = id;
            this.memberId = memberId;
            this.resumeId = resumeId;
            this.coverLetterId = coverLetterId;
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
            this.dateSaved = dateSaved.toString();

            // https://www.baeldung.com/jackson-object-mapper-tutorial
            ObjectMapper objectMapper = new ObjectMapper();
            // If using Map<String, Object> for dateApplied/Posted
//            this.dateApplied = dateApplied != null ? objectMapper.readValue(dateApplied, new TypeReference<Map<String, Object>>(){}) : null;
//            this.datePosted = datePosted != null ? objectMapper.readValue(datePosted, new TypeReference<Map<String, Object>>(){}) : null;
            // Otherwise, use these
            this.dateApplied = dateApplied != null ? objectMapper.readValue(dateApplied.toString(), YearMonthDateDto.class) : null;
            this.datePosted = datePosted != null ? objectMapper.readValue(datePosted, YearMonthDateDto.class) : null;
            this.jobStatus = jobStatus;
            // readValue has an error without the try-catch. Intellij suggested
            //   the try-catch as a solution, which removed the error
            this.links = links != null ? objectMapper.readValue(links, String[].class) : null;
            this.foundFrom = foundFrom;
            this.resume = resume != null ? objectMapper.readValue(resume, ResponseResumeDto.class) : null;
            this.coverLetter = coverLetter != null ? objectMapper.readValue(coverLetter, ResponseCoverLetterDto.class) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getMemberId() { return memberId; }
    public UUID getResumeId() { return resumeId; }
    public UUID getCoverLetterId() { return coverLetterId; }
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
    public YearMonthDateDto getDateApplied() { return dateApplied; }
    public YearMonthDateDto getDatePosted() { return datePosted; }
    public String getJobStatus() { return jobStatus; }
    public String[] getLinks() { return links; }
    public String getFoundFrom() { return foundFrom; }
    public ResponseResumeDto getResume() { return resume; }
    public ResponseCoverLetterDto getCoverLetter() { return coverLetter; }

    @Override
    public boolean equals(Object comparedObject) {
        // Variables in same memory location so they are the same
        if (this == comparedObject) {
            return true;
        }

        // if comparedObject is not a ResponseJobDto, can't be the same object
        if (!(comparedObject instanceof ResponseJobDto)) {
            return false;
        }

        ResponseJobDto comparedResponseJobDto = (ResponseJobDto) comparedObject;

        // Compare instance variables of each object
        // https://stackoverflow.com/questions/11271554/compare-two-objects-in-java-with-possible-null-values
        // - Compare objects with null check
        return Objects.equals(this.getId(), comparedResponseJobDto.getId()) &&
            Objects.equals(this.getMemberId(), comparedResponseJobDto.getMemberId()) &&
            Objects.equals(this.getResumeId(), comparedResponseJobDto.getResumeId()) &&
            Objects.equals(this.getCoverLetterId(), comparedResponseJobDto.getCoverLetterId()) &&
            Objects.equals(this.getTitle(), comparedResponseJobDto.getTitle()) &&
            Objects.equals(this.getCompanyName(), comparedResponseJobDto.getCompanyName()) &&
            Objects.equals(this.getJobDescription(), comparedResponseJobDto.getJobDescription()) &&
            Objects.equals(this.getNotes(), comparedResponseJobDto.getNotes()) &&
            Objects.equals(this.getIsRemote(), comparedResponseJobDto.getIsRemote()) &&
            Objects.equals(this.getSalaryMin(), comparedResponseJobDto.getSalaryMin()) &&
            Objects.equals(this.getSalaryMax(), comparedResponseJobDto.getSalaryMax()) &&
            Objects.equals(this.getCountry(), comparedResponseJobDto.getCountry()) &&
            Objects.equals(this.getUsState(), comparedResponseJobDto.getUsState()) &&
            Objects.equals(this.getCity(), comparedResponseJobDto.getCity()) &&
            Objects.equals(this.getDateSaved(), comparedResponseJobDto.getDateSaved()) &&
            Objects.equals(this.getDateApplied(), comparedResponseJobDto.getDateApplied()) &&
            Objects.equals(this.getDatePosted(), comparedResponseJobDto.getDatePosted()) &&
            Objects.equals(this.getJobStatus(), comparedResponseJobDto.getJobStatus()) &&
            // https://stackoverflow.com/questions/8777257/equals-vs-arrays-equals-in-java
            // - Compare content of arrays
            Arrays.equals(this.getLinks(), comparedResponseJobDto.getLinks()) &&
            Objects.equals(this.getFoundFrom(), comparedResponseJobDto.getFoundFrom()) &&
            Objects.equals(this.getResume(), comparedResponseJobDto.getResume()) &&
            Objects.equals(this.getCoverLetter(), comparedResponseJobDto.getCoverLetter());
    }
}

// Just keep for now for printing values inside constructor
//        System.out.printf("id: %s\n", id);
//        System.out.printf("memberId: %s\n", memberId);
//        System.out.printf("resumeId: %s\n", resumeId);
//        System.out.printf("coverLetterId: %s\n", coverLetterId);
//        System.out.printf("title: %s\n", title);
//        System.out.printf("companyName: %s\n", companyName);
//        System.out.printf("jobDescription: %s\n", jobDescription);
//        System.out.printf("notes: %s\n", notes);
//        System.out.printf("isRemote: %s\n", isRemote);
//        System.out.printf("salaryMin: %d\n", salaryMin);
//        System.out.printf("salaryMin: %d\n", salaryMax);
//        System.out.printf("country: %s\n", country);
//        System.out.printf("usState: %s\n", usState);
//        System.out.printf("city: %s\n", city);
//        System.out.printf("dateSaved: %s\n", dateSaved);
//        System.out.printf("dateApplied: {\n\tyear: %s,\n\tmonth: %s,\n\tdate: %s\n}\n", dateApplied.get("year"), dateApplied.get("month"), dateApplied.get("date"));
//        System.out.printf("datePosted: {\n\tyear: %s,\n\tmonth: %s,\n\tdate: %s\n}\n", datePosted.get("year"), datePosted.get("month"), datePosted.get("date"));
//        System.out.printf("jobStatus: %s\n", jobStatus);
//        // Links
//        System.out.println("Printing links:");
//        for (int i = 0; i < links.length; i++) {
//            System.out.printf("link %d: %s\n", i, links[i]);
//        }
//        System.out.println("DONE printing links:");
//        System.out.printf("foundFrom: %s\n", foundFrom);
//        System.out.printf("resume: %s\n", resume);
//        System.out.printf("resume: %s\n", coverLetter);
