package com.qu1cksave.qu1cksave_backend.job;

// TODO: Later, I need to use an Entity from Hibernate
//   Do I also need a separate DAO?
public record Job(
    String id,
    String memberId,
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
    // Stored as timestamptz in the database
    String dateSaved,
//    YearMonthDate dateApplied,
//    YearMonthDate datePosted,
    String jobStatus,
    String[] links,
    String foundFrom
) {
}
