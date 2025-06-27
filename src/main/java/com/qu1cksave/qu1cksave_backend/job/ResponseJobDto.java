package com.qu1cksave.qu1cksave_backend.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qu1cksave.qu1cksave_backend.coverletter.ResponseCoverLetterDto;
import com.qu1cksave.qu1cksave_backend.resume.ResponseResumeDto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
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
            // IMPORTANT: When obtaining from the database via native query,
            //   objects such YearMonthDateDto, String[], and the custom file
            //   classes are passed to the constructor as String.
            ObjectMapper objectMapper = new ObjectMapper();
            // ------------ OLD (Keep for reference) -------------
            // If using Map<String, Object> for dateApplied/Posted. Otherwise,
            //   use the ones after
//            this.dateApplied = dateApplied != null ? objectMapper.readValue(dateApplied, new TypeReference<Map<String, Object>>(){}) : null;
//            this.datePosted = datePosted != null ? objectMapper.readValue(datePosted, new TypeReference<Map<String, Object>>(){}) : null;
            // ------------------------------
            if (dateApplied != null) {
                // When obtaining from the database via native query, this is a
                //   String.
                if (dateApplied instanceof String) {
                    this.dateApplied = objectMapper.readValue((String) dateApplied, YearMonthDateDto.class);
                // When deserializing, such as during integeration tests
                } else {
                    // When I do:
                    //   this.dateApplied = (YearMonthDateDto) dateApplied;
                    // I get:
                    //   java.lang.ClassCastException: class java.util.LinkedHashMap cannot be cast to class com.qu1cksave.qu1cksave_backend.job.YearMonthDateDto (java.util.LinkedHashMap is in module java.base of loader 'bootstrap'; com.qu1cksave.qu1cksave_backend.job.YearMonthDateDto is in unnamed module of loader 'app')
                    LinkedHashMap dateAppliedMap = (LinkedHashMap) dateApplied;
                    this.dateApplied = new YearMonthDateDto(
                        (Integer) dateAppliedMap.get("year"),
                        (Integer) dateAppliedMap.get("month"),
                        (Integer) dateAppliedMap.get("date")
                    );
                }
            }
            if (datePosted != null) {
                if (datePosted instanceof String) {
                    this.datePosted = objectMapper.readValue((String) datePosted, YearMonthDateDto.class);
                } else {
                    LinkedHashMap datePostedMap = (LinkedHashMap) datePosted;
                    this.datePosted = new YearMonthDateDto(
                        (Integer) datePostedMap.get("year"),
                        (Integer) datePostedMap.get("month"),
                        (Integer) datePostedMap.get("date")
                    );
                }
            }
//            this.dateApplied = dateApplied != null ? objectMapper.readValue(dateApplied, YearMonthDateDto.class) : null;
//            this.datePosted = datePosted != null ? objectMapper.readValue(datePosted, YearMonthDateDto.class) : null;

            this.jobStatus = jobStatus;

            if (links != null) {
                if (links instanceof String) {
                    this.links = objectMapper.readValue((String) links, String[].class);
                } else {
                    //  java.lang.ClassCastException: class java.util.ArrayList cannot be cast to class [Ljava.lang.String; (java.util.ArrayList and [Ljava.lang.String; are in module java.base of loader 'bootstrap')
                    // this.links = (String[]) links;
                    // https://www.baeldung.com/java-convert-string-arraylist-array\
                    ArrayList arrLstLinks = (ArrayList) links;
                    String[] linksArr = new String[arrLstLinks.size()];
                    arrLstLinks.toArray(linksArr);
                    this.links = linksArr;
                }
            }
//            this.links = links != null ? objectMapper.readValue(links, String[].class) : null;

            this.foundFrom = foundFrom;

            if (resume != null) {
                if (resume instanceof String) {
                    // NOTE: Have not tested if this works with
                    //   ResponseResumeDto having a byteArrayAsArray
                    this.resume = objectMapper.readValue((String) resume, ResponseResumeDto.class);
                } else { // Used when deserializing during tests
                    LinkedHashMap resumeMap = (LinkedHashMap) resume;
                    String fileId = (String) resumeMap.get("id");
                    String fileMemberId = (String) resumeMap.get("member_id");

                    // Important: There could be cases where resume != null but
                    //   the job doesn't actually have a resume. One example is
                    //   when using query that has a left join and builds the
                    //   object from that other table via json_build_object.
                    // - This returns a resume where all the fields are null
                    //   if the job doesn't have a resume
                    // - I can just check if fileId and fileMemberId are null
                    //   since UUID.fromString throws an exception if given a
                    //   null value
                    // IMPORTANT: When resume instanceof String, the
                    //   objectMapper creates a ResponseResumeDto with null
                    //   fields, so this one should do the same
                    this.resume = new ResponseResumeDto(
                        fileId != null ? UUID.fromString(fileId): null,
                        fileMemberId != null ? UUID.fromString(fileMemberId): null,
                        (String) resumeMap.get("file_name"),
                        (String) resumeMap.get("mime_type"),
                        resumeMap.get("byte_array_as_array")
                    );
                }
            }

            if (coverLetter != null) {
                if (coverLetter instanceof String) {
                    this.coverLetter = objectMapper.readValue((String) coverLetter, ResponseCoverLetterDto.class);
                } else {
                    LinkedHashMap coverLetterMap = (LinkedHashMap) coverLetter;
                    String fileId = (String) coverLetterMap.get("id");
                    String fileMemberId = (String) coverLetterMap.get("member_id");

                    this.coverLetter = new ResponseCoverLetterDto(
                        fileId != null ? UUID.fromString(fileId): null,
                        fileMemberId != null ? UUID.fromString(fileMemberId): null,
                        (String) coverLetterMap.get("file_name"),
                        (String) coverLetterMap.get("mime_type"),
                        coverLetterMap.get("byte_array_as_array")
                    );
                }
            }
//            this.resume = resume != null ? objectMapper.readValue(resume, ResponseResumeDto.class) : null;
//            this.coverLetter = coverLetter != null ? objectMapper.readValue(coverLetter, ResponseCoverLetterDto.class) : null;
        } catch (JsonProcessingException e) {
            // TODO: I can just have the method throws JsonProcessingException
            //  since I'm not adding a custom message here
            //  - I needed to have this throw a runtime exception since
            //    Transactional only rolls back on runtime exceptions.
            //    However, adding the annotation on the config below changes that
            //      @EnableTransactionManagement(rollbackOn=ALL_EXCEPTIONS)
            //  - UPDATE: I'm keeping this since adding throws JsonProcessingException
            //    requires adding a throws again in the service, then the controller, ...
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
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Note that this uses camelCase key names
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

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

    // The native query with a left join and json_build_object fills jobs
    //   with no resume/cover letter with a resume/cover letter with empty
    //   fields. Ex. "resume": { "id": null, "member_id": null, ...
    public void nullifyEmptyFiles() {
        // If resume has is not null but has empty fields, just set it to null
        if (resume != null &&
            resume.getId() == null &&
            resume.getMemberId() == null &&
            resume.getFileName() == null &&
            resume.getMimeType() == null &&
            resume.getByteArrayAsArray() == null
        ) {
            resume = null;
        }

        // If coverLetter has empty fields, just set it to null
        if (coverLetter != null &&
            coverLetter.getId() == null &&
            coverLetter.getMemberId() == null &&
            coverLetter.getFileName() == null &&
            coverLetter.getMimeType() == null &&
            coverLetter.getByteArrayAsArray() == null
        ) {
            coverLetter = null;
        }
    }

    // https://stackoverflow.com/questions/56810827/find-indexof-of-an-object-in-custom-list-using-one-attribute
    // - Using indexOf is an option, but would need to implement equals and
    //   hashCode. ME: I'm just creating a static method instead
    // Returns the first job from the given list that matches the given id
    // Returns null if job can't be found
    // I won't add null list check, wrong type, etc.
    public static ResponseJobDto findById(
        List<ResponseJobDto> jobs,
        UUID id
    ) {
        for (ResponseJobDto job : jobs) {
            // IMPORTANT: Use .equals instead of ==
            if (job.getId().equals(id)) {
                return job;
            }
        }

        return null;
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