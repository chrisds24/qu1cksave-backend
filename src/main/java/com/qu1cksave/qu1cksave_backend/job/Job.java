package com.qu1cksave.qu1cksave_backend.job;

import com.qu1cksave.qu1cksave_backend.coverletter.CoverLetter;
import com.qu1cksave.qu1cksave_backend.resume.Resume;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
// https://stackoverflow.com/questions/60021815/why-has-javax-persistence-api-been-replaced-by-jakarta-persistence-api-in-spring
// - javax.persistence.Transient and the one below serve the same purpose
import jakarta.persistence.Transient;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "job")
public class Job {
    // TODO:
    //  If when querying and only the id of the other table (Ex. resumeId) is
    //    in the current table, but not the object itself (Ex. resume), how
    //    should I annotate that column (if it should even be a column of the
    //    entity in the first place?)
    //    - Note that this is a different case as with byteArrayAsArray for
    //      Resume and CoverLetter, since that byteArrayAsArray isn't obtained
    //      from any table. So I can just create a Resume/CoverLetter DTO that
    //      has that field
    //    - Search "Hibernate entity field not column of a table"
    // TODO: Read the case below, which is what I'm dealing with
    //   - You get rid of a database column because it's possible to fetch
    //     that from a different table with a JOIN. Now the entity has less
    //     information than the DTO
    //     (THIS IS MY USE CASE)
    //  UPDATE:
    //    - (DOUBLE CHECK THIS) I won't need associations since the tables
    //      don't really store entities from another table, only the id
    //    - I'l probably need custom queries (try HQL? If not, use actual SQL)
    //    - Could also be useful: https://docs.jboss.org/hibernate/orm/7.0/introduction/html_single/Hibernate_Introduction.html#join-fetch
    //    - There are also Native Queries: https://docs.jboss.org/hibernate/orm/7.0/introduction/html_single/Hibernate_Introduction.html#native-queries

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private UUID id;

    // https://docs.jboss.org/hibernate/orm/7.0/introduction/html_single/Hibernate_Introduction.html#associations
    // - ManyToOne associations are eagerly fetched by default (unlike
    //   OneToMany and ManyToMany, which is not what I want for my use case
    // - Though, a Job does not have a Member column so this isn't a problem
    @Column(name = "member_id", nullable = false)
    private UUID memberId;

    @Column(name = "resume_id")
    private UUID resumeId;

    // IMPORTANT: Hibernate entity field not column of a table
    // https://stackoverflow.com/questions/52942906/can-a-jpa-entity-have-a-field-not-mapped-to-a-db-column
    // https://stackoverflow.com/questions/4662582/make-hibernate-ignore-instance-variables-that-are-not-mapped
    // https://stackoverflow.com/questions/1281952/what-is-the-easiest-way-to-ignore-a-jpa-field-during-persistence
    // - To ignore a field, annotate it with @Transient so it will not be
    //   mapped by hibernate. but then jackson will not serialize the field
    //   when converting to JSON. If you need mix JPA with JSON(omit by JPA but
    //   still include in Jackson) use @JsonInclude.
    // - TODO: ME: Though, I won't need JsonInclude here since I'm converting
    //    to a DTO first.
    @Transient
    private Resume resume; // NOT a column of the table

    @Column(name = "cover_letter_id")
    private UUID coverLetterId;

    @Transient
    private CoverLetter coverLetter; // NOT a column of the table

    // https://docs.jboss.org/hibernate/orm/7.0/introduction/html_single/Hibernate_Introduction.html#regular-column-mappings
    // - Has info on @Column annotation members, such as nullable
    // - default value for nullable is true
    @Column(nullable = false)
    private String title;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "job_description")
    private String jobDescription;

    private String notes;

    @Column(name = "is_remote", nullable = false)
    private String isRemote;

    // https://stackoverflow.com/questions/3154582/why-do-i-get-a-null-value-was-assigned-to-a-property-of-primitive-type-setter-o
    // - Need to use object type (Ex. Integer instead of int)
    @Column(name = "salary_min")
    private Integer salaryMin;

    @Column(name = "salary_max")
    private Integer salaryMax;

    private String country;

    @Column(name = "us_state")
    private String usState;

    private String city;

    // Stored as timestamptz in the database
    @Column(name = "date_saved", nullable = false)
    private String dateSaved;

    // https://docs.jboss.org/hibernate/orm/7.0/introduction/html_single/Hibernate_Introduction.html#embeddable-objects
    // - Need an embeddable type
    // https://docs.jboss.org/hibernate/orm/7.0/introduction/html_single/Hibernate_Introduction.html#mapping-embeddables
    // - Map to jsonb
    @Column(name = "date_applied")
    @JdbcTypeCode(SqlTypes.JSON)
    private YearMonthDate dateApplied;

    @Column(name = "date_posted")
    @JdbcTypeCode(SqlTypes.JSON)
    private YearMonthDate datePosted;

    @Column(name = "job_status", nullable = false)
    private String jobStatus;

    // https://docs.jboss.org/hibernate/orm/7.0/introduction/html_single/Hibernate_Introduction.html#mapping-embeddables
    // - This is about mapping embeddable types to JSONB. So not sure if it
    //   works here
    // - Also mentions somewhere that JSON arrays aren't supported
    // https://hibernate.org/orm/quickly/
    // - If you have a field or property that maps to a single column, but its
    //   type isn’t one of the basic types build in to Hibernate, you can use an AttributeConverter
    // TODO: Keep this one in mind in case it causes errors
    //   - Also, should this be String[] or List<String> ???
    //   - UPDATE: This seems to work fine
    @JdbcTypeCode(SqlTypes.JSON)
    private String[] links;

    @Column(name = "found_from")
    private String foundFrom;

    // Constructors

    protected Job() {}

    public Job(
        UUID id,
        UUID memberId,
        UUID resumeId,
        Resume resume,
        UUID coverLetterId,
        CoverLetter coverLetter,
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

    // TODO: I heard I can use Lombok to generate getters and setters

    // Getters
    public UUID getId() { return id; }
    public UUID getMemberId() { return memberId; }
    public UUID getResumeId() { return resumeId; }
    public Resume getResume() { return resume; }
    public UUID getCoverLetterId() { return coverLetterId; }
    public CoverLetter getCoverLetter() { return coverLetter; }
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

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setMemberId(UUID memberId) { this.memberId = memberId; }
    public void setResumeId(UUID resumeId) { this.resumeId = resumeId; }
    public void setResume(Resume resume) { this.resume = resume; }
    public void setCoverLetterId(UUID coverLetterId) { this.coverLetterId = coverLetterId; }
    public void setCoverLetter(CoverLetter coverLetter) { this.coverLetter = coverLetter; }
    public void setTitle(String title) { this.title = title; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setIsRemote(String isRemote) { this.isRemote = isRemote; }
    public void setSalaryMin(Integer salaryMin) { this.salaryMin = salaryMin; }
    public void setSalaryMax(Integer salaryMax) { this.salaryMax = salaryMax; }
    public void setCountry(String country) { this.country = country; }
    public void setUsState(String usState) { this.usState = usState; }
    public void setCity(String city) { this.city = city; }
    public void setDateSaved(String dateSaved) { this.dateSaved = dateSaved; }
    public void setDateApplied(YearMonthDate dateApplied) { this.dateApplied = dateApplied; }
    public void setDatePosted(YearMonthDate datePosted) { this.datePosted = datePosted; }
    public void setJobStatus(String jobStatus) { this.jobStatus = jobStatus; }
    public void setLinks(String[] links) { this.links = links; }
    public void setFoundFrom(String foundFrom) { this.foundFrom = foundFrom; }
}


// IMPORTANT:
// https://stackoverflow.com/questions/69850710/dto-dao-and-entity-is-entity-needed-best-pratice-with-those-3
// - The entity is supposed to be a representation of a table,
//   so I shouldn't include things that wouldn't be in it here
// - I could use a JobDto to be a representation of a Job that could have
//   Resumes/CoverLetters
// https://www.reddit.com/r/SpringBoot/comments/zlphkn/difference_between_dto_and_entity_and_dao/
// - but if you are working with Entities inside the service you need to map
//   the (live) Entity to a DTO which you return to the caller (e.g.
//   controller)
// - so those are simple Mappings from one Class to the other Class (e.g.
//   MapStruct helps so you don't have to manually write the mapping code)
// - ME: I remember reading somewhere that DAOs and Repositories are used
//   interchangeably
// https://www.reddit.com/r/learnprogramming/comments/12sbyfp/if_an_entity_and_a_response_object_dto_have/
// - Why use a DTO?
//   -- What happens down the line if you change the database schema and
//      the Entity has more fields, or needs to change the type of one of
//      the fields? That change could end up breaking the frontend.Example:
//      TODO: Read the case below, which is what I'm dealing with
//      + You get rid of a database column because it's possible to fetch
//        that from a different table with a JOIN. Now the entity has less
//        information than the DTO
//        (THIS IS MY USE CASE)
// https://stackoverflow.com/questions/43882484/why-not-hierarchy-from-entity-to-create-dto#:~:text=Usually%2C%20a%20DTO%20will%20be,to%20access%20all%20that%20state.
// - Don't extend an Entity to get a DTO from it
// https://www.baeldung.com/java-entity-vs-dto
// - There can be a separate ObjectCreationDto and ObjectResponseDto
//   -- Ex. UserCreationDto (without id) and UserResponseDto (now has id)
// - We can manually create a Mapper class (Ex. UserMapper), or even
//   better, use MapStruct
// - return userRepository.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
//   -- TODO: Why use stream and collect again?
// https://www.baeldung.com/mapstruct
// - Though, I heard a few times that MapStruct is deprecated.
// - I just created a manual mapper
// TODO: Search "hibernate entity field not in table site:stackoverflow.com"
// - https://www.baeldung.com/jpa-hibernate-associations
// - https://stackoverflow.com/questions/63473073/hibernate-have-a-field-that-is-not-peristed-but-can-be-pulled-from-db
// - https://stackoverflow.com/questions/4008418/hibernate-add-a-property-in-my-class-that-is-not-mapped-to-a-db-table
// - https://stackoverflow.com/questions/52942906/can-a-jpa-entity-have-a-field-not-mapped-to-a-db-column
// - https://stackoverflow.com/questions/4662582/make-hibernate-ignore-instance-variables-that-are-not-mapped
// - TODO: Can also search "hibernate entity less fields than dto"