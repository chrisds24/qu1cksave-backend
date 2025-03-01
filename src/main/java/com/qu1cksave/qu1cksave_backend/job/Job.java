package com.qu1cksave.qu1cksave_backend.job;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "job")
public class Job {
    // TODO:
    //  Specify optional and required types
    //  What is the appropriate association I should use?
    //    https://docs.jboss.org/hibernate/orm/7.0/introduction/html_single/Hibernate_Introduction.html#associations
    //  Need to create a data source

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // TODO: Edit this to include associations
    @Column(name = "member_id")
    private UUID memberId;

//    private String resumeId;
//    private Resume resume;
//    private String coverLetterId;
//    private CoverLetter coverLetter;

    private String title;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "job_description")
    private String jobDescription;

    private String notes;

    @Column(name = "is_remote")
    private String isRemote;

    @Column(name = "salary_min")
    private int salaryMin;

    @Column(name = "salary_max")
    private int salaryMax;

    private String country;

    @Column(name = "us_state")
    private String usState;

    private String city;

    // Stored as timestamptz in the database
    @Column(name = "date_saved")
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

    @Column(name = "job_status")
    private String jobStatus;

    // https://docs.jboss.org/hibernate/orm/7.0/introduction/html_single/Hibernate_Introduction.html#mapping-embeddables
    // - This is about mapping embeddable types to JSONB. So not sure if it
    //   works here
    // - Also mentions somewhere that JSON arrays aren't supported
    @JdbcTypeCode(SqlTypes.JSON)
    private String[] links;

    @Column(name = "found_from")
    private String foundFrom;

    // Constructors

    protected Job() {}

    public Job(
        UUID id,
        UUID memberId,
//    String resumeId,
//    Resume resume,
//    String coverLetterId,
//    CoverLetter coverLetter,
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
//        this.resumeId = resumeId;
//        this.resume = resume;
//        this.coverLetterId = coverLetterId;
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

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setMemberId(UUID memberId) { this.memberId = memberId; }
    public void setTitle(String title) { this.title = title; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setIsRemote(String isRemote) { this.isRemote = isRemote; }
    public void setSalaryMin(int salaryMin) { this.salaryMin = salaryMin; }
    public void setSalaryMax(int salaryMax) { this.salaryMax = salaryMax; }
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