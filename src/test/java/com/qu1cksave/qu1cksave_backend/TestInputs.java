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

	// title
	public static String testNewJobWithFilesWrongType1 = """
			{
				"title": 5,
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

	// salary_min
	public static String testNewJobWithFilesWrongType2 = """
			{
				"title": "test swe",
				"company_name": "test company",
				"job_description": "test job description",
				"notes": "test notes",
				"is_remote": "Remote",
				"salary_min": "75000",
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

	// Date applied year
	public static String testNewJobWithFilesWrongType3 = """
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
					"year": true,
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

	// links (2nd element is 25, which is not a String)
	public static String testNewJobWithFilesWrongType4 = """
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
				"links": ["https://www.linkedin.com/jobs/view/4125105888/?alternateChannel=search&refId=L%2FaJTJBeDgAXxHgZ%2B3%2FBAw%3D%3D&trackingId=GQdu9ntQnwrk1Hxp2qSNAQ%3D%3D", 25],
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

	// Resume
	public static String testNewJobWithFilesWrongType5 = """
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
				"found_from": "LinkedIn",
				"resume": false,
				"cover_letter": {
				    "file_name": "My_Test_CoverLetter.docx",
				    "mime_type": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
				    "byte_array_as_array": [5,4,3,2,1]
			    }
			}
		""";

	// Resume byte_array_as_array
	public static String testNewJobWithFilesWrongType6 = """
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
				"found_from": "LinkedIn",
				"resume": {
				    "file_name": "My_Test_Resume.pdf",
				    "mime_type": "application/pdf",
				    "byte_array_as_array": ["hey","there","delilah","song"]
			    },
				"cover_letter": {
				    "file_name": "My_Test_CoverLetter.docx",
				    "mime_type": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
				    "byte_array_as_array": [5,4,3,2,1]
			    }
			}
		""";

	// Cover letter byte_array_as_array
	public static String testNewJobWithFilesWrongType7 = """
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
				"found_from": "LinkedIn",
				"resume": {
				    "file_name": "My_Test_Resume.pdf",
				    "mime_type": "application/pdf",
				    "byte_array_as_array": [1,2,3,4,5]
			    },
				"cover_letter": {
				    "file_name": "My_Test_CoverLetter.docx",
				    "mime_type": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
				    "byte_array_as_array": "Kevin Durant"
			    }
			}
		""";
}
