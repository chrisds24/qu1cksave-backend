package com.qu1cksave.qu1cksave_backend.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qu1cksave.qu1cksave_backend.coverletter.ResponseCoverLetterDto;
import com.qu1cksave.qu1cksave_backend.resume.ResponseResumeDto;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

// https://www.baeldung.com/jackson-nested-values
// - Register deserializer to class
// NOTE: (6/12/25) Not using custom deserializer since Jackson seems to keep
//   trying to use my constructor
//@JsonDeserialize(using = ResponseJobDtoDeserializer.class)
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

    private UUID id; // NOT NULLABLE                          // 1
    private UUID memberId; // NOT NULLABLE                    // 2
    private UUID resumeId;                                    // 3
    private UUID coverLetterId;                               // 5
    private String title; // NOT NULLABLE                     // 7
    private String companyName; // NOT NULLABLE               // 8
    private String jobDescription;                            // 9
    private String notes;                                     // 10
    private String isRemote; // NOT NULLABLE                  // 11
    private Integer salaryMin;                                // 12
    private Integer salaryMax;                                // 13
    private String country;                                   // 14
    private String usState;                                   // 15
    private String city;                                      // 16
    private String dateSaved; // NOT NULLABLE                 // 17
//    private final Map<String, Object> dateApplied;                        // 18
//    private final Map<String, Object> datePosted;                         // 19
    private YearMonthDateDto dateApplied;                        // 18
    private YearMonthDateDto datePosted;                         // 19
    private String jobStatus; // NOT NULLABLE                 // 20
    private String[] links;                                   // 21
    private String foundFrom;                                 // 22
    private ResponseResumeDto resume;                                 // 4
    private ResponseCoverLetterDto coverLetter;                       // 6

    // No arg constructor
//    public ResponseJobDto() {
//
//    }

    // Constructors
    // https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html#projections.dtos
    // - When using class-based projections and there's more than one
    //   constructor, need further hints for DTO projections such as
    //   @PersistenceCreator
    // - I didn't end up needing to use projections
    // https://www.baeldung.com/jackson-annotations
    // - JsonCreator used when deserializing. Need to mark fields with
    //   JsonProperty
//    @JsonCreator // Not needed. I read Java 8? does this automatically
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
        // IMPORTANT: date_applied, date_posted, links, resume, and coverLetter
        //   are originally String. But I switched to Object since Jackson
        //   sees Object (which is actually a YearMonthDateDto) for dateApplied
        //   and datePosted, String[] for links, and ResponseResumeDto (or
        //   cover letter) for the files when deserializing in the tests
//        @JsonProperty("date_applied") String dateApplied,
        @JsonProperty("date_applied") Object dateApplied,
//        @JsonProperty("date_posted") String datePosted,
        @JsonProperty("date_posted") Object datePosted,
        @JsonProperty("job_status") String jobStatus,
// Needs to be a string since no automatic conversion from JSON array
//   to a String array
//        @JsonProperty("links") String[] links,
//        @JsonProperty("links") String links,
        @JsonProperty("links") Object links,
        @JsonProperty("found_from") String foundFrom,
        // This doesn't automatically convert to ResponseResumeDto since it
        //   can't cast String to this. So using String instead
//        @JsonProperty("resume") ResponseResumeDto resume,
//        @JsonProperty("cover_letter") ResponseCoverLetterDto coverLetter
//        @JsonProperty("resume") String resume,
//        @JsonProperty("cover_letter") String coverLetter
        @JsonProperty("resume") Object resume,
        @JsonProperty("cover_letter") Object coverLetter
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
            // ------------ OLD (Keep for reference) -------------
            // If using Map<String, Object> for dateApplied/Posted. Otherwise,
            //   use the ones after
//            this.dateApplied = dateApplied != null ? objectMapper.readValue(dateApplied, new TypeReference<Map<String, Object>>(){}) : null;
//            this.datePosted = datePosted != null ? objectMapper.readValue(datePosted, new TypeReference<Map<String, Object>>(){}) : null;
            // ------------------------------
            if (dateApplied != null) {
                if (dateApplied instanceof String) {
                    this.dateApplied = objectMapper.readValue((String) dateApplied, YearMonthDateDto.class);
                } else if (dateApplied instanceof YearMonthDateDto) { // Object so it must be a YearMonthDateDto
                    this.dateApplied = (YearMonthDateDto) dateApplied;
                }
            }
            if (datePosted != null) {
                if (datePosted instanceof String) {
                    this.datePosted = objectMapper.readValue((String) datePosted, YearMonthDateDto.class);
                } else if (datePosted instanceof YearMonthDateDto) { // Object so it must be a YearMonthDateDto
                    this.datePosted = (YearMonthDateDto) datePosted;
                }
            }
//            this.dateApplied = dateApplied != null ? objectMapper.readValue(dateApplied, YearMonthDateDto.class) : null;
//            this.datePosted = datePosted != null ? objectMapper.readValue(datePosted, YearMonthDateDto.class) : null;

            this.jobStatus = jobStatus;

            if (links != null) {
                if (links instanceof String) {
                    this.links = objectMapper.readValue((String) links, String[].class);
                } else if (links instanceof String[]) {
                    this.links = (String[]) links;
                }
            }
//            this.links = links != null ? objectMapper.readValue(links, String[].class) : null;
            this.foundFrom = foundFrom;

            // TODO: Double-check later if this works using tests
            if (resume != null) {
                if (resume instanceof String) {
                    this.resume = objectMapper.readValue((String) resume, ResponseResumeDto.class);
                } else if (resume instanceof ResponseResumeDto) {
                    this.resume = (ResponseResumeDto) resume;
                }
            }
            if (coverLetter != null) {
                if (coverLetter instanceof String) {
                    this.coverLetter = objectMapper.readValue((String) coverLetter, ResponseCoverLetterDto.class);
                } else if (coverLetter instanceof ResponseCoverLetterDto) {
                    this.coverLetter = (ResponseCoverLetterDto) coverLetter;
                }
            }
//            this.resume = resume != null ? objectMapper.readValue(resume, ResponseResumeDto.class) : null;
//            this.coverLetter = coverLetter != null ? objectMapper.readValue(coverLetter, ResponseCoverLetterDto.class) : null;
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

// ------------------------ Deserialization notes --------------------
//  Jackson private setters when deserializing fields
//  - https://stackoverflow.com/questions/43821319/how-does-jackson-set-private-properties-without-setters
//    -- Very good read
//   https://stackoverflow.com/questions/58556027/how-does-jackson-deserializer-work-by-default
//   - Good info
//  Does Jackson have a way to indicate which constructor to use?
//  - There can only be one creator :(
//    -- From https://stackoverflow.com/questions/15931082/how-to-deserialize-a-class-with-overloaded-constructors-using-jsoncreator
//       + From searching: Failed to evaluate Jackson serialization for type com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Conflicting property-based creators: already had implicit creator [constructor for site:stackoverflow.com
//         * Note: I removed some parts of the error message to search for it
//
//
// It's the expectBody(ResponseJobDto.class) that's causing:
//   com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot deserialize value of type `java.lang.String` from Object value (token `JsonToken.START_OBJECT`)
//         at [Source: UNKNOWN; line: 1, column: 429] (through reference chain: com.qu1cksave.qu1cksave_backend.job.ResponseJobDto["date_applied"])
// - https://stackoverflow.com/questions/19389723/can-not-deserialize-instance-of-java-lang-string-out-of-start-object-token
//   -- Has a good explanation on why this happens. Something about the
//      setter seeing a JSON_OBJECT instead of String. In my case, the
//      dto constructor is probably getting passed a JSON_OBJECT
// - UPDATE: (6/12/25) If I try to check jsonPath date_applied, it is
//   indeed passing an Object of format {year=2020, month=2, date=5}
//
//
//  Search: "deserialize nested json jackson"
//  - https://www.baeldung.com/jackson-nested-values
//    -- @JsonDeserialize could be useful. More proper way of doing it
//  ME:
//  - When I call the JPA method using my custom native query, it
//    fills the fields accordingly but it's not doing deserialization,
//    which is why having String dateApplied in the constructor works
//    -- Reminder: I needed to use String for dateApplied since that's
//       what the native query is returning for that field
//  - But when deserializing during the tests, it seems that the dto
//    constructor is getting passed a JSON_OBJECT, even though it's
//    expecting a String (before I changed it to Object)
//
// Error after adding deserializer:
//    java.lang.RuntimeException: com.fasterxml.jackson.databind.exc.MismatchedInputException: No content to map due to end-of-input at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); line: 1]
// - https://stackoverflow.com/questions/26925058/no-content-to-map-due-to-end-of-input-jackson-parser
