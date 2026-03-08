package com.qu1cksave.qu1cksave_backend;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestInputs {
    public static String testNewOrEditJobNoFiles = """
			{
				"title": "test swe",
				"company_name": "test company",
				"job_description": "test job description",
				"notes": "test notes",
				"is_remote": "Remote",
				"salary_min": 75000,
				"salary_max": 120000,
				"country": "US",
				"us_state": "CA",
				"city": "San Diego",
				"date_applied": {
					"year": 2025,
					"month": 4,
					"date": 9
				},
				"date_posted": {
					"year": 2025,
					"month": 4,
					"date": 8
				},
				"job_status": "Applied",
				"links": ["https://www.linkedin.com/jobs/view/4125105888/?alternateChannel=search&refId=L%2FaJTJBeDgAXxHgZ%2B3%2FBAw%3D%3D&trackingId=GQdu9ntQnwrk1Hxp2qSNAQ%3D%3D", "https://jobs.ashbyhq.com/clinical-notes-ai/3d10314e-9af5-4ec3-8cb7-9edd8e32a3e9"],
				"found_from": "LinkedIn"
			}
		""";

    public static String testNewOrEditJobWithFiles = """
			{
				"title": "test swe with files",
				"company_name": "test company",
				"job_description": "test job description",
				"notes": "test notes",
				"is_remote": "Remote",
				"salary_min": 75000,
				"salary_max": 120000,
				"country": "US",
				"us_state": "CA",
				"city": "San Diego",
				"date_applied": {
					"year": 2025,
					"month": 4,
					"date": 9
				},
				"date_posted": {
					"year": 2025,
					"month": 4,
					"date": 8
				},
				"job_status": "Applied",
				"links": ["https://www.linkedin.com/jobs/view/4125105888/?alternateChannel=search&refId=L%2FaJTJBeDgAXxHgZ%2B3%2FBAw%3D%3D&trackingId=GQdu9ntQnwrk1Hxp2qSNAQ%3D%3D", "https://jobs.ashbyhq.com/clinical-notes-ai/3d10314e-9af5-4ec3-8cb7-9edd8e32a3e9"],
				"found_from": "LinkedIn",
				"resume": {
				    "file_name": "My_Test_Resume.pdf",
				    "mime_type": "application/pdf",
				    "byte_array_as_array": [1,2,3,4,5]
			    },
				"cover_letter": {
				    "file_name": "My_Test_CoverLetter.docx",
				    "mime_type": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
				    "byte_array_as_array": [5,4,3,2,1]
			    }
			}
		""";

	// Returns this edited job with the given resumeId and
	//   coverLetterId.
	// Note: This is necessary since the resumeId and coverLetterId
	//   can't be known in advance
	public static String testEditJobWithFilesEdited(
		String resumeId,
		String coverLetterId
	) {
		// https://stackoverflow.com/questions/21232185/simple-way-templating-multiline-strings-in-java-code
		return """
			{
				"resume_id": "%s",
				"cover_letter_id": "%s",
				"title": "test swe with files edited",
				"company_name": "test company",
				"job_description": "test job description",
				"notes": "test notes",
				"is_remote": "Remote",
				"salary_min": 75000,
				"salary_max": 120000,
				"country": "US",
				"us_state": "CA",
				"city": "San Diego",
				"date_applied": {
					"year": 2025,
					"month": 4,
					"date": 9
				},
				"date_posted": {
					"year": 2025,
					"month": 4,
					"date": 8
				},
				"job_status": "Applied",
				"links": ["https://www.linkedin.com/jobs/view/4125105888/?alternateChannel=search&refId=L%%2FaJTJBeDgAXxHgZ%%2B3%%2FBAw%%3D%%3D&trackingId=GQdu9ntQnwrk1Hxp2qSNAQ%%3D%%3D", "https://jobs.ashbyhq.com/clinical-notes-ai/3d10314e-9af5-4ec3-8cb7-9edd8e32a3e9"],
				"found_from": "LinkedIn",
				"resume": {
					"file_name": "My_Edited_Test_Resume.docx",
					"mime_type": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
					"byte_array_as_array": [2,4,6,8,10]
				},
				"cover_letter": {
					"file_name": "My_Edited_Test_CoverLetter.pdf",
					"mime_type": "application/pdf",
					"byte_array_as_array": [10,8,6,4,2]
				}
			}
		""".formatted(resumeId, coverLetterId);
	}

	public static String testEditJobWithResumeEdited(
		String resumeId,
		String coverLetterId
	) {
		// https://stackoverflow.com/questions/21232185/simple-way-templating-multiline-strings-in-java-code
		return """
			{
				"resume_id": "%s",
				"cover_letter_id": "%s",
				"title": "test swe with files edited",
				"company_name": "test company",
				"job_description": "test job description",
				"notes": "test notes",
				"is_remote": "Remote",
				"salary_min": 75000,
				"salary_max": 120000,
				"country": "US",
				"us_state": "CA",
				"city": "San Diego",
				"date_applied": {
					"year": 2025,
					"month": 4,
					"date": 9
				},
				"date_posted": {
					"year": 2025,
					"month": 4,
					"date": 8
				},
				"job_status": "Applied",
				"links": ["https://www.linkedin.com/jobs/view/4125105888/?alternateChannel=search&refId=L%%2FaJTJBeDgAXxHgZ%%2B3%%2FBAw%%3D%%3D&trackingId=GQdu9ntQnwrk1Hxp2qSNAQ%%3D%%3D", "https://jobs.ashbyhq.com/clinical-notes-ai/3d10314e-9af5-4ec3-8cb7-9edd8e32a3e9"],
				"found_from": "LinkedIn",
				"resume": {
					"file_name": "mYRESU_mE_isALLc_orrUPTed.docx",
					"mime_type": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
					"byte_array_as_array": [2,4,6,8,10]
				},
				"cover_letter": {
					"file_name": "My_Edited_Test_CoverLetter.pdf",
					"mime_type": "application/pdf",
					"byte_array_as_array": [10,8,6,4,2]
				}
			}
		""".formatted(resumeId, coverLetterId);
	}

	// NOTE: I didn't bother setting keep_resume and keep_cover_letter for the
	//   previous test inputs, but it shouldn't be a problem since none of them
	//   would go to the case where:
	//   - (resume == null) but (resume_id != null)
	//   - Here, there's no uploaded resume but there's a resume id meaning that
	//     the job has a resume
	//   - So we'll need to have keep_resume to indicate if we should keep that
	//     resume or delete it
	//   - Same thing applies for cover letters

	public static String testEditJobWithFilesNotEdited(
		String resumeId,
		String coverLetterId
	) {
		// https://stackoverflow.com/questions/21232185/simple-way-templating-multiline-strings-in-java-code
		return """
			{
				"resume_id": "%s",
				"cover_letter_id": "%s",
				"title": "test swe with files edited",
				"company_name": "test company",
				"job_description": "test job description",
				"notes": "test notes",
				"is_remote": "Remote",
				"salary_min": 75000,
				"salary_max": 120000,
				"country": "US",
				"us_state": "CA",
				"city": "San Diego",
				"date_applied": {
					"year": 2025,
					"month": 4,
					"date": 9
				},
				"date_posted": {
					"year": 2025,
					"month": 4,
					"date": 8
				},
				"job_status": "Applied",
				"links": ["https://www.linkedin.com/jobs/view/4125105888/?alternateChannel=search&refId=L%%2FaJTJBeDgAXxHgZ%%2B3%%2FBAw%%3D%%3D&trackingId=GQdu9ntQnwrk1Hxp2qSNAQ%%3D%%3D", "https://jobs.ashbyhq.com/clinical-notes-ai/3d10314e-9af5-4ec3-8cb7-9edd8e32a3e9"],
				"found_from": "LinkedIn",
				"keep_resume": true,
				"keep_cover_letter": true
			}
		""".formatted(resumeId, coverLetterId);
	}

	// This one doesn't have the keepResume and keepCoverLetter
	// Sanity check to ensure that
	public static String testEditJobWithFilesNotEditedSanityCheck(
		String resumeId,
		String coverLetterId
	) {
		// https://stackoverflow.com/questions/21232185/simple-way-templating-multiline-strings-in-java-code
		return """
			{
				"resume_id": "%s",
				"cover_letter_id": "%s",
				"title": "test swe with files edited",
				"company_name": "test company",
				"job_description": "test job description",
				"notes": "test notes",
				"is_remote": "Remote",
				"salary_min": 75000,
				"salary_max": 120000,
				"country": "US",
				"us_state": "CA",
				"city": "San Diego",
				"date_applied": {
					"year": 2025,
					"month": 4,
					"date": 9
				},
				"date_posted": {
					"year": 2025,
					"month": 4,
					"date": 8
				},
				"job_status": "Applied",
				"links": ["https://www.linkedin.com/jobs/view/4125105888/?alternateChannel=search&refId=L%%2FaJTJBeDgAXxHgZ%%2B3%%2FBAw%%3D%%3D&trackingId=GQdu9ntQnwrk1Hxp2qSNAQ%%3D%%3D", "https://jobs.ashbyhq.com/clinical-notes-ai/3d10314e-9af5-4ec3-8cb7-9edd8e32a3e9"],
				"found_from": "LinkedIn"
			}
		""".formatted(resumeId, coverLetterId);
	}

	public static String testEditJobWithFilesDeleted(
		String resumeId,
		String coverLetterId
	) {
		// https://stackoverflow.com/questions/21232185/simple-way-templating-multiline-strings-in-java-code
		return """
			{
				"resume_id": "%s",
				"cover_letter_id": "%s",
				"title": "test swe with files edited",
				"company_name": "test company",
				"job_description": "test job description",
				"notes": "test notes",
				"is_remote": "Remote",
				"salary_min": 75000,
				"salary_max": 120000,
				"country": "US",
				"us_state": "CA",
				"city": "San Diego",
				"date_applied": {
					"year": 2025,
					"month": 4,
					"date": 9
				},
				"date_posted": {
					"year": 2025,
					"month": 4,
					"date": 8
				},
				"job_status": "Applied",
				"links": ["https://www.linkedin.com/jobs/view/4125105888/?alternateChannel=search&refId=L%%2FaJTJBeDgAXxHgZ%%2B3%%2FBAw%%3D%%3D&trackingId=GQdu9ntQnwrk1Hxp2qSNAQ%%3D%%3D", "https://jobs.ashbyhq.com/clinical-notes-ai/3d10314e-9af5-4ec3-8cb7-9edd8e32a3e9"],
				"found_from": "LinkedIn",
				"keep_resume": false,
				"keep_cover_letter": false
			}
		""".formatted(resumeId, coverLetterId);
	}
}

// NOTE: (6/4/25) Tests I need
//  - UPDATE: (3/8/26) I'll move the notes to the TestInputs file to make
//    this extremely long file shorter
//  - Get one job, job exists
//  - Get one job, job doesn't exist
//    -- Covers case when user specifies an id of a job that doesn't belong
//       to them (since single jobs are found by id and userId)
//  - ??? Get one job, no id provided
//    -- Just goes to regular /jobs route but without a query
//  --------------------------
//  - Create job w/o files, then get that job
//  - Create job w/ files, then get that job
//  - Create invalid job, missing required fields
//  - Create invalid job, extra fields
//   * For the last 2, I could use some kind of API validation, filters,
//     or custom code. API validation would be ideal, but custom code could
//     be enough for now
//     -- Need to manually check not having required fields
//        + Having the wrong type just causes an error in Java
//     -- The extra fields one would actually just cause an error in Java
//  --------------------------
//  - Get multiple jobs, userId query param is the logged in user's id
//  - Get multiple jobs, userId query param is NOT the logged in user's id
//  - ??? Get multiple jobs, no userId query param provided
//    -- Spring/Java should throw out an error for this. Wonder what it
//       exactly is?
//  ---------------------------
//  - Delete job, job exists
//  - Delete job, job doesn't exist (due to stale list)
//    -- Here, stale refers to the list not having up to date jobs
//  - Delete job, but job is stale (see description in edit job tests)
//    -- The job and the up to date files get deleted since the delete uses
//       the file ids from the job obtained from the database
//  ---------------------------
//  - Edit job w/o files, then get that job
//  - Edit job w/ resume and cover letter added, then get that job
//  - Edit job w/ resume and cover letter edited, then get that job
//  - Edit job w/ resume and cover letter deleted, then get that job
//  - Stale job tests
//    +++ Note, a job where its resume id and cover letter id are the same
//        as the latest version is not considered stale
//    +++ How does a job become stale? Let's say there's tab A and tab B,
//        where tab A contains the stale job. The job is then edited in tab
//        B so that at least one of the files' id's are now different from
//        what tab A has
//        * Note that the following also applies to cover letter
//        * MAKE TESTS FOR THESE 3
//    1.) Job has no resume, then edited to have a resume. Edit job again,
//       but using the old job w/o the resume
//    2.) Job has a resume, then edited to have a different resume id (by
//       removing the resume, then adding one again). Edit job again,
//       but using the old job w/ the old resume id
//    3.) Job has a resume, then edited to have no resume. Edit job again,
//       but using the old job w/ the resume
//    +++ Other possible issues:
//    -- Same resume id. User then attempts to edit resume by editing the
//       stale job
//       + Resume gets updated to this new resume, which is appropriate
//         * Note that both the metadata and the file in S3 would get
//           updated accordingly. The job also gets updated using the
//           updates made to the stale job
//    -- Same resume id. User then attempts to remove resume by editing the
//       stale job.
//       + Resume gets removed from S3 and database, as expected
//    +++ IMPORTANT: I went with just leaving it to the user to not do
//        their work in stale tabs, then just having checks where errors
//        could occur.
//        - An alternative would have been to have a timestamp for the
//          most recent update timestamp, and not allow edits/deletes if
//          using a stale job (aka an outdated update timestamp)
//  ---------------------------
//  LATER: Not logged in tests for each endpoint
