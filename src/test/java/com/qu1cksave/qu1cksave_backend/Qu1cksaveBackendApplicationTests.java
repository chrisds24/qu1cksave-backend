package com.qu1cksave.qu1cksave_backend;

import com.qu1cksave.qu1cksave_backend.coverletter.ResponseCoverLetterDto;
import com.qu1cksave.qu1cksave_backend.job.ResponseJobDto;
import com.qu1cksave.qu1cksave_backend.resume.ResponseResumeDto;
import com.qu1cksave.qu1cksave_backend.user.ResponseUserDto;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
	locations = "classpath:application-product-integrationtest.properties"
)
@Testcontainers
// Run tests in a specific order: https://www.baeldung.com/junit-5-test-order
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Qu1cksaveBackendApplicationTests {
	// Not needed
//    @LocalServerPort
//    private int port;

	// Non-static field 'postgresUser' cannot be referenced from a static context
	// - Need to make this static to fix
	// - @Value doesn't work
//	@Value("${POSTGRES_DB}")
	private static final String postgresDb = System.getenv("POSTGRES_DB");

	private static String postgresUser = System.getenv("POSTGRES_USER");

	private static String postgresPassword = System.getenv("POSTGRES_PASSWORD");

	// This will use a random port
	@Container
	static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
		.withDatabaseName(postgresDb)
		.withUsername(postgresUser)
		.withPassword(postgresPassword)
		// sql folder needs to be in resources
		.withInitScripts("sql/schema.sql", "sql/data.sql")
		.withStartupTimeout(Duration.of(3, MINUTES));

	// Not needed. But could be useful for extra setup
//	@BeforeAll
//	static void beforeAll() {
//		postgreSQLContainer.start();
//	}
//
//	@AfterAll
//	static void afterAll() {
//		postgreSQLContainer.stop();
//	}

	@DynamicPropertySource
	static void postgresqlProperties(DynamicPropertyRegistry registry) {
		// Need this since the postgreSQLContainer uses a random port
		registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		// Don't need these since already set to this
//        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
//        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
	}

	@Autowired
	private WebTestClient webTestClient;

	// TODO: (6/4/25) Tests I need
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

	// Used by some create job tests
	private void badRequestBodyCreateTest(String json) {
		// Create the job
		this.webTestClient
			.post()
			.uri("/job")
			.contentType(MediaType.APPLICATION_JSON)
			// No resume and cover letter
			.bodyValue(json)
			.header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.exchange()
			.expectStatus()
			.isBadRequest()
			.expectBody()
			.isEmpty()
		;
	}

	// Used by some edit job tests
	private WebTestClient.BodySpec<ResponseJobDto, ?> getJobRequestReturningBodySpec(String id) {
		return this.webTestClient
			.get()
			.uri("/job/" + id)
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentType(MediaType.APPLICATION_JSON)
			.expectBody(ResponseJobDto.class);
	}

	// Used by some edit job tests
	private ResponseJobDto editJobRequestReturningJob(
		String jobId,
		String jobJson
	) {
		return this.webTestClient
			.put()
			.uri("/job/" + jobId)
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(jobJson)
			.header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentType(MediaType.APPLICATION_JSON)
			.expectBody(ResponseJobDto.class)
			.returnResult()
			.getResponseBody()
		;
	}

	// Used by some edit job tests
	private void editJobRequestUsingStaleJob(
		String jobId,
		String jobJson
	) {
		this.webTestClient
			.put()
			.uri("/job/" + jobId)
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(jobJson)
			.header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.exchange()
			.expectStatus()
			.isEqualTo(409)
			.expectBody()
			.isEmpty()
		;
	}

	@Test
	@Order(1)
	void contextLoads() {
	}

	// ------------------- GET ONE JOB TESTS -------------------
	// Get one job tests
	//  - Get one job, job exists
	//  - Get one job, job doesn't exist
	//    -- Covers case when user specifies an id of a job that doesn't belong
	//       to them (since single jobs are found by id and userId)
	//  - ??? Get one job, no id provided
	//    -- Just goes to regular /job route but without a query

	@Test
	@Order(2)
	void getOneJobNoFiles() { // Get one job, job exists
		// '018eae1f-d0e7-7fa8-a561-6aa358134f7e'
		// Expected: 'Software Engineer', 'Microsoft', very long description
		this.webTestClient
			.get()
			.uri("/job/018eae1f-d0e7-7fa8-a561-6aa358134f7e")
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.title").isEqualTo("Software Engineer")
			.jsonPath("$.company_name").isEqualTo("Microsoft")
			.jsonPath("$.resume.id").isEmpty()
			.jsonPath("$.resume").isNotEmpty()
			.jsonPath("$.resume.member_id").isEmpty()
			.jsonPath("$.resume.file_name").isEmpty()
			.jsonPath("$.resume.mime_type").isEmpty()
			.jsonPath("$.cover_letter").isNotEmpty()
			.jsonPath("$.cover_letter.id").isEmpty()
			.jsonPath("$.cover_letter.member_id").isEmpty()
			.jsonPath("$.cover_letter.file_name").isEmpty()
			.jsonPath("$.cover_letter.mime_type").isEmpty()
		;
	}

	@Test
	@Order(3)
	void getOneJobWithFiles() { // Get one job, job exists
		this.webTestClient
			.get()
			.uri("/job/018eae28-8323-7918-b93a-6cdb9d189686")
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.title").isEqualTo("Software Engineer")
			.jsonPath("$.company_name").isEqualTo("Fake Company")
			.jsonPath("$.resume.file_name").isEqualTo("ChristianDelosSantos_Resume.pdf")
			.jsonPath("$.cover_letter.file_name").isEqualTo("ChristianDelosSantos_CoverLetter.pdf")
		;
	}

	// Get one job, job doesn't exist
	@Test
	@Order(4)
	void getNonExistentJob() {
		this.webTestClient
			.get()
			.uri("/job/deadbeef-abab-6161-7c7c-fefe58135858")
			.exchange()
			.expectStatus()
			.isNotFound()
			.expectBody()
			.consumeWith(result -> {
                assertNull(result.getResponseBody());
			});
	}

	// When user specifies an id of a job that doesn't belong
	//   to them (since single jobs are found by id and userId)
	// Should return 404 instead of 403, since single jobs are obtained by id
	//   and userId. I also don't want to give away the existence of a job's id
	@Test
	@Order(5)
	void getForbiddenJob() {
		this.webTestClient
			.get()
			.uri("/job/a14ead6c-d173-1111-a001-2717322ebd12")
			.exchange()
			.expectStatus()
			.isNotFound()
			.expectBody()
			.isEmpty(); // Alternative to consumeWith then asserting
	}

	// ----------------- CREATE JOB TESTS ---------------------

	// Create job tests
	//  - Create job w/o files, then get that job
	//  - Create job w/ files, then get that job
	//
	//  Create job, invalid body
	//  - Create job, wrong types
	//  - Create invalid job, missing required fields
	//  - Create invalid job, extra fields
	//  - ...
	//   * For the last 2, I could use some kind of API validation, filters,
	//     or custom code. API validation would be ideal, but custom code could
	//     be enough for now
	//     -- Need to manually check not having required fields
	//        + Having the wrong type just causes an error in Java
	//     -- The extra fields one would actually just cause an error in Java

	@Test
	@Order(6)
	void createJobNoFilesThenGetThatJob() {
		// Create the job
		ResponseJobDto responseJobDto = this.webTestClient
			.post()
			.uri("/job")
			.contentType(MediaType.APPLICATION_JSON)
			// No resume and cover letter
			.bodyValue(TestInputs.testNewOrEditJobNoFiles)
			.header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.exchange()
			.expectStatus()
			.isCreated()
			.expectHeader()
			.contentType(MediaType.APPLICATION_JSON)
			.expectBody(ResponseJobDto.class)
			.returnResult()
			.getResponseBody()
		;

		// Make sure create returned the expected value
		assertNotNull(responseJobDto);
		// Note that the toString uses camelCase key names
		assertNotNull(responseJobDto.getId());
		assertEquals(UUID.fromString("269a3d55-4eee-4a2e-8c64-e1fe386b76f8"), responseJobDto.getMemberId());
		assertNotNull(responseJobDto.getDateSaved());
		assertEquals("test swe", responseJobDto.getTitle());
		assertEquals(4, responseJobDto.getDateApplied().getMonth());
		assertEquals(8, responseJobDto.getDatePosted().getDate());
		assertEquals(
			"https://jobs.ashbyhq.com/clinical-notes-ai/3d10314e-9af5-4ec3-8cb7-9edd8e32a3e9",
			responseJobDto.getLinks()[1]
		);

		// It's the native query used by getJob(get one job) that returns
		//   a file with empty fields if the job doesn't that file
		assertNull(responseJobDto.getResumeId());
		assertNull(responseJobDto.getResume());
		assertNull(responseJobDto.getCoverLetterId());
		assertNull(responseJobDto.getCoverLetter());

		// Get the job
		this.webTestClient
			.get()
			.uri("/job/" + responseJobDto.getId())
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentType(MediaType.APPLICATION_JSON)
			.expectBody(ResponseJobDto.class)
			.consumeWith(result -> {
				ResponseJobDto res = result.getResponseBody();
				assertNotNull(res);
				// The native query used by getJob(get one job) returns a file
				//   with empty fields if the job doesn't that file. Need to
				//   set those to null so comparison could work properly
				res.nullifyEmptyFiles();
				assertEquals(responseJobDto, res);
			});
	}

	@Test
	@Order(7)
	void createJobWithFilesThenGetThatJob() {
		// Create the job
		ResponseJobDto responseJobDto = this.webTestClient
			.post()
			.uri("/job")
			.contentType(MediaType.APPLICATION_JSON)
			// No resume and cover letter
			.bodyValue(TestInputs.testNewOrEditJobWithFiles)
			.header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.exchange()
			.expectStatus()
			.isCreated()
			.expectHeader()
			.contentType(MediaType.APPLICATION_JSON)
			.expectBody(ResponseJobDto.class)
			.returnResult()
			.getResponseBody()
		;

		assertNotNull(responseJobDto);

		assertNotNull(responseJobDto.getResumeId());
		ResponseResumeDto resume = responseJobDto.getResume();
		assertNotNull(resume);
		assertNotNull(resume.getId());
		assertEquals(UUID.fromString("269a3d55-4eee-4a2e-8c64-e1fe386b76f8"), resume.getMemberId());
		assertEquals("My_Test_Resume.pdf", resume.getFileName());

		assertNotNull(responseJobDto.getCoverLetterId());
		ResponseCoverLetterDto coverLetter = responseJobDto.getCoverLetter();
		assertNotNull(coverLetter);
		assertNotNull(coverLetter.getId());
		assertEquals(UUID.fromString("269a3d55-4eee-4a2e-8c64-e1fe386b76f8"), coverLetter.getMemberId());
		assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", coverLetter.getMimeType());

		this.webTestClient
			.get()
			.uri("/job/" + responseJobDto.getId())
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentType(MediaType.APPLICATION_JSON)
			.expectBody(ResponseJobDto.class)
			.consumeWith(result -> {
				assertEquals(responseJobDto, result.getResponseBody());
			})
		;

		// TODO: Get the resume and cover letter to make sure they're there
		//  ...
		//  ...
	}

	// TODO: (6/16/25)
	//  - These are not working properly. Some still create an object even
	//    if the wrong type was passed (Probably because Jackson might be
	//    auto-converting some of them)
	//  - I'll just use Open API Schema Validation
//	@Test
//	@Order(8)
//	void createJobWithFilesWrongRequestBodyTypes() {
//		// Integer title
//		badRequestBodyCreateTest(TestInputsWrongTypesRequestBody.testNewJobIntegerTitle);
//		// String salary_min
//		badRequestBodyCreateTest(TestInputsWrongTypesRequestBody.testNewJobStringSalaryMin);
//		// Boolean date_applied.year
//		badRequestBodyCreateTest(TestInputsWrongTypesRequestBody.testNewJobDateAppliedBooleanYear);
//		// links 2nd element is a string
//		badRequestBodyCreateTest(TestInputsWrongTypesRequestBody.testNewJobLinksStringElement);
//		// resume is a Boolean
//		badRequestBodyCreateTest(TestInputsWrongTypesRequestBody.testNewJobBooleanResume);
//		// resume.byte_array_as_array has String elements
//		badRequestBodyCreateTest(TestInputsWrongTypesRequestBody.testNewJobResumeByteArrayHasStrings);
//		// cover_letter byte_array_as_array is a String
//		badRequestBodyCreateTest(TestInputsWrongTypesRequestBody.testNewJobCoverLetterStringByteArray);
//	}

	// TODO: (6/16/25)
	//  - These are not working properly. @NotNull doesn't seem to work when
	//    the object is nested
	//  - I'll just use Open API Schema Validation
//	@Test
//	@Order(9)
//	void createJobWithFilesMissingRequestBodyFields() {
//		badRequestBodyCreateTest(TestInputsMissingFieldsRequestBody.testNewJobMissingTitle);
//		badRequestBodyCreateTest(TestInputsMissingFieldsRequestBody.testNewJobNullTitle);
//
//		// Returns 201 instead of 400 which it should
//		badRequestBodyCreateTest(TestInputsMissingFieldsRequestBody.testNewJobDateAppliedMissingYear);
//		// Returns 500 instead of 400 which it should
//		badRequestBodyCreateTest(TestInputsMissingFieldsRequestBody.testNewJobResumeMissingFileName);
//	}

//	@Test
//	@Order(10)
//	void createJobWithFilesExtraRequestBodyFields() {
//		// TODO
//	}

	// ----------------- GET MULTIPLE JOBS TESTS ---------------------
	//  - Get multiple jobs, userId query param is the logged in user's id
	//  - Get multiple jobs, userId query param is NOT the logged in user's id
	//  - ??? Get multiple jobs, no userId query param provided
	//    -- Spring/Java should throw out an error for this. Wonder what it
	//       exactly is?

	@Test
	@Order(11)
	void getMultipleJobsNonEmptyList() {
		this.webTestClient
			.get()
			// Molly Member's id
			.uri("/job?id=269a3d55-4eee-4a2e-8c64-e1fe386b76f8")
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentType(MediaType.APPLICATION_JSON)
			.expectBodyList(ResponseJobDto.class)
			.consumeWith(result -> {
				List<ResponseJobDto> jobs = result.getResponseBody();
				assertNotNull(jobs);
				assertThat(jobs).isNotEmpty(); // Did I make the correct import?

				// Find one without a resume
				ResponseJobDto job1 = ResponseJobDto.findById(
					jobs,
					UUID.fromString("018eae1f-d0e7-7fa8-a561-6aa358134f7e")
				);
				assertNotNull(job1);
				assertEquals("Software Engineer", job1.getTitle());
				assertEquals("Microsoft", job1.getCompanyName());
				assertNull(job1.getResumeId());
				// If a job doesn't have a file, get multiple jobs endpoint
				//   returns that job with that file but with all null fields
				assertNotNull(job1.getResume());
				job1.nullifyEmptyFiles(); // If all fields are null, resume = null
				assertNull(job1.getResume());

				// Find one with a resume
				ResponseJobDto job2 = ResponseJobDto.findById(
					jobs,
					UUID.fromString("018eae28-8323-7918-b93a-6cdb9d189686")
				);
				assertNotNull(job2);
				assertEquals("Software Engineer", job2.getTitle());
				assertEquals("Fake Company", job2.getCompanyName());
				assertEquals(UUID.fromString(
					"2bbb76f8-46c8-e2a4-2bbb-3d55e1fe386b"),
					job2.getResumeId()
				);
				ResponseResumeDto job2Resume = job2.getResume();
				assertNotNull(job2Resume);
				assertEquals(UUID.fromString(
					"2bbb76f8-46c8-e2a4-2bbb-3d55e1fe386b"),
					job2Resume.getId()
				);
				assertEquals(
					"ChristianDelosSantos_Resume.pdf",
					job2Resume.getFileName()
				);
			});
	}

	// TODO: Uncomment this once Spring Security authentication added
	//  - Anna actually has a job in data.sql, which I commented out
//	@Test
//	@Order(12)
//	void getMultipleJobsEmptyList() {
//		this.webTestClient
//			.get()
//			// Anna Admin's id
//			.uri("/job?id=4604289c-b8fe-4560-8960-4da47fdfef94")
//			.exchange()
//			.expectStatus()
//			.isOk()
//			.expectHeader()
//			.contentType(MediaType.APPLICATION_JSON)
//			.expectBodyList(ResponseJobDto.class)
//			.consumeWith(result -> {
//				List<ResponseJobDto> jobs = result.getResponseBody();
//				assertNotNull(jobs);
//				assertEquals(0, jobs.size());
//			});
//	}

	@Test
	@Order(13)
	void getMultipleJobsWrongUser() {
		this.webTestClient
			.get()
			.uri("/job?id=4604289c-b8fe-4560-8960-4da47fdfef94")
			.exchange()
			.expectStatus()
			// Wrong user returns not found for security reasons
			.isNotFound()
			.expectBody()
			.isEmpty()
		;
	}

	@Test
	@Order(14)
	void getMultipleJobsNonExistentUser() {
		this.webTestClient
			.get()
			.uri("/job?id=1234abcd-dead-beef-daed-11112323aaaa")
			.exchange()
			.expectStatus()
			// Although the query returns a non-empty list if given a non-
			//   existent user id, this endpoint would return 404 since the
			//   check for if the provided user id and logged in id matches
			//   fails, which returns a 404
			.isNotFound()
			.expectBody()
			.isEmpty()
		;
	}

	@Test
	@Order(15)
	void getMultipleJobsNoUserIdProvided() {
		this.webTestClient
			.get()
			.uri("/job")
			.exchange()
			.expectStatus()
			.isBadRequest()
			.expectBody()
			.isEmpty()
		;
	}

	// -------------------------- DELETE JOB TESTS ----------------------------
	//  - Delete job, job exists
	//  - Delete job, job doesn't exist (due to stale list)
	//    -- Here, stale refers to the list not having up to date jobs
	//  - Delete job, but job is stale (see description in edit job tests)
	//    -- The job and the up to date files get deleted since the delete uses
	//       the file ids from the job obtained from the database

	@Test
	@Order(16)
	void deleteJobWithoutFilesThenGetThatJob() {
		// Delete job
		this.webTestClient
			.delete()
			.uri("/job/018eae1f-d0e7-7fa8-a561-6aa358134f7e")
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.title").isEqualTo("Software Engineer")
			.jsonPath("$.company_name").isEqualTo("Microsoft")
		;

		// Make sure it's no longer there
		this.webTestClient
			.get()
			.uri("/job/018eae1f-d0e7-7fa8-a561-6aa358134f7e")
			.exchange()
			.expectStatus()
			.isNotFound()
			.expectBody()
			.isEmpty()
		;
	}

	@Test
	@Order(17)
	void deleteJobWithFilesThenGetThatJob() {
		// INSERT INTO job(id, member_id, resume_id, cover_letter_id, title, company_name, is_remote, job_status) VALUES
		//   ('323e9876-8018-b93a-8197-beefbeefbeef', '269a3d55-4eee-4a2e-8c64-e1fe386b76f8', '3cccfefe-46c8-e2a4-46c8-dadae1fedada'
		//   , '2bbbefef-46c8-e2a4-2bbb-beefdadafefe','To Delete Job Title', 'To Delete Job Company', 'Hybrid', 'Not Applied');
		// INSERT INTO resume(id, member_id, file_name, mime_type) VALUES
		//   ('3cccfefe-46c8-e2a4-46c8-dadae1fedada', '269a3d55-4eee-4a2e-8c64-e1fe386b76f8', 'TODELETE_Resume.docx', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document');
		// INSERT INTO cover_letter(id, member_id, file_name, mime_type) VALUES
		//   ('2bbbefef-46c8-e2a4-2bbb-beefdadafefe', '269a3d55-4eee-4a2e-8c64-e1fe386b76f8', 'TODELETE_CoverLetter.pdf', 'application/pdf');
		// Delete job
		ResponseJobDto job = this.webTestClient
			.delete()
			.uri("/job/323e9876-8018-b93a-8197-beefbeefbeef")
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentType(MediaType.APPLICATION_JSON)
//			.expectBody()
//			.jsonPath("$.title").isEqualTo("To Delete Job Title")
//			.jsonPath("$.company_name").isEqualTo("To Delete Job Company")
			.expectBody(ResponseJobDto.class)
			.returnResult()
			.getResponseBody()
		;

		assertNotNull(job);
		assertEquals("To Delete Job Title", job.getTitle());
		assertEquals("To Delete Job Company", job.getCompanyName());

		// Make sure it's no longer there
		this.webTestClient
			.get()
			.uri("/job/23e9876-8018-b93a-8197-beefbeefbeef")
			.exchange()
			.expectStatus()
			.isNotFound()
			.expectBody()
			.isEmpty()
		;

		// TODO: Get the resume and cover letter from each respective endpoint
		this.webTestClient
			.get()
			.uri("/resume/" + job.getResumeId())
			.exchange()
			.expectStatus()
			.isNotFound()
			.expectBody()
			.isEmpty()
		;

		this.webTestClient
			.get()
			.uri("/coverLetter/" + job.getCoverLetterId())
			.exchange()
			.expectStatus()
			.isNotFound()
			.expectBody()
			.isEmpty()
		;
	}

	@Test
	@Order(18)
	// Delete a job that doesn't exist
	// In real life, this happens if a user's job list is stale and
	//   they click delete on a job that no longer exists
	void deleteNonExistentJob() {
		// Delete job
		this.webTestClient
			.delete()
			// This job has already been deleted in the previous test
			.uri("/job/018eae1f-d0e7-7fa8-a561-6aa358134f7e")
			.exchange()
			.expectStatus()
			.isNotFound()
			.expectBody()
			.isEmpty()
		;
	}

	// NOT NEEDED
//	// Delete job, but job is stale (see description in edit job tests)
//	// - The job and the up to date files get deleted since the delete uses
//	//   the file ids from the job obtained from the database
//	// NOTE: Can't really test this one here since there's no point to.
//	//   Even if I create a stale job, I'm only sending the id in the uri.
//	//   So, the job being stale does not matter.
//	@Test
//	void deleteStaleJob() {
//		// NOT NEEDED
//	}

	// ------------------------- EDIT JOB TESTS -------------------------------
	//  - Edit job w/o files, then get that job
	//  - Edit job w/ resume and cover letter added, then get that job
	//  - Edit job w/ resume and cover letter edited, then get that job
	//  - Edit job w/ resume and cover letter deleted, then get that job
	//  - Stale job tests

	// Job edited has no files, and no files have been added during edit
	@Test
	@Order(19)
	void editJobWithNoFilesThenGetThatJob() {
		// Original before edit
		// id: '018ead6b-d160-772d-a001-2606322ebd1c'
		// member_id: '269a3d55-4eee-4a2e-8c64-e1fe386b76f8'
		// title: 'Software Engineer, Quantum Error Correction, Quantum AI'
		// date_applied.month: 3
		// date_posted.date: 29
		// links[1]: N/A

		// Edit the job
		this.webTestClient
			.put()
			.uri("/job/018ead6b-d160-772d-a001-2606322ebd1c")
			.contentType(MediaType.APPLICATION_JSON)
			// No resume and cover letter
			.bodyValue(TestInputs.testNewOrEditJobNoFiles)
			.header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.id").isEqualTo("018ead6b-d160-772d-a001-2606322ebd1c")
			.jsonPath("$.member_id").isEqualTo("269a3d55-4eee-4a2e-8c64-e1fe386b76f8")
			.jsonPath("$.title").isEqualTo("test swe")
			.jsonPath("$.date_applied.month").isEqualTo(4)
			.jsonPath("$.date_posted.date").isEqualTo(8)
			.jsonPath("$.links[1]").isEqualTo("https://jobs.ashbyhq.com/clinical-notes-ai/3d10314e-9af5-4ec3-8cb7-9edd8e32a3e9");

		// Get the job
		this.webTestClient
			.get()
			.uri("/job/018ead6b-d160-772d-a001-2606322ebd1c")
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.id").isEqualTo("018ead6b-d160-772d-a001-2606322ebd1c")
			.jsonPath("$.member_id").isEqualTo("269a3d55-4eee-4a2e-8c64-e1fe386b76f8")
			.jsonPath("$.title").isEqualTo("test swe")
			// Should not be this
//			.jsonPath("$.title").isEqualTo("Software Engineer, Quantum Error Correction, Quantum AI")
			.jsonPath("$.date_applied.month").isEqualTo(4)
			.jsonPath("$.date_posted.date").isEqualTo(8)
			.jsonPath("$.links[1]").isEqualTo("https://jobs.ashbyhq.com/clinical-notes-ai/3d10314e-9af5-4ec3-8cb7-9edd8e32a3e9");
	}

	// Job edited has no files, but files are added during edit
	@Test
	@Order(20)
	void editJobWithFilesAddedThenGetThatJob() {
		// Original before edit
		// id: "018ead6b-d160-772d-a001-2606322ebd1c"
		// member_id: "269a3d55-4eee-4a2e-8c64-e1fe386b76f8"
		// title: "test swe"
		// date_applied.month: 4
		// date_posted.date: 8
		// links[1]: "https://jobs.ashbyhq.com/clinical-notes-ai/3d10314e-9af5-4ec3-8cb7-9edd8e32a3e9"

		ResponseJobDto job = editJobRequestReturningJob(
			"018ead6b-d160-772d-a001-2606322ebd1c",
			TestInputs.testNewOrEditJobWithFiles
		);

		// Ensure that changed data have changed and unchanged data haven't
		assertNotNull(job);
		assertEquals(UUID.fromString("018ead6b-d160-772d-a001-2606322ebd1c"), job.getId());
		assertEquals(UUID.fromString("269a3d55-4eee-4a2e-8c64-e1fe386b76f8"), job.getMemberId());
		assertEquals("test swe with files", job.getTitle());
		assertEquals(4, job.getDateApplied().getMonth());
		assertEquals(8, job.getDatePosted().getDate());
		assertEquals(
			"https://jobs.ashbyhq.com/clinical-notes-ai/3d10314e-9af5-4ec3-8cb7-9edd8e32a3e9",
			job.getLinks()[1]
		);
		// Ensure files are added with the correct data
		assertNotNull(job.getResumeId());
		assertNotNull(job.getCoverLetterId());
		assertEquals(job.getResumeId(), job.getResume().getId());
		assertEquals(job.getCoverLetterId(), job.getCoverLetter().getId());
		assertEquals("My_Test_Resume.pdf", job.getResume().getFileName());
		assertEquals(
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
			job.getCoverLetter().getMimeType()
		);

		// Get the job
		getJobRequestReturningBodySpec("018ead6b-d160-772d-a001-2606322ebd1c")
			.consumeWith(result -> {
				assertEquals(job, result.getResponseBody());
			});

		// TODO: Get the resume and cover letter to ensure they are added
	}


	// ********* STALE JOB TEST 1 *********
	// - Job now has a resume, but is being edited using a stale version
	//   without a resume (so no resume id)
	@Test
	@Order(21)
	void editJobWithFilesUsingStaleJobWithoutFiles() {
		// First, get the job to be edited to obtain its file ids
		ResponseJobDto origJob = getJobRequestReturningBodySpec(
			"018ead6b-d160-772d-a001-2606322ebd1c")
			.returnResult()
			.getResponseBody();
		assertNotNull(origJob);
		// Edit the job with the stale job that doesn't have a resume id
		editJobRequestUsingStaleJob(
			"018ead6b-d160-772d-a001-2606322ebd1c",
			// Note: The previous test used TestInputs.testNewOrEditJobWithFiles
			//   which added files to this job
			TestInputs.testNewOrEditJobNoFiles
		);
		// Ensure that the job hasn't been changed
		getJobRequestReturningBodySpec("018ead6b-d160-772d-a001-2606322ebd1c")
			.consumeWith(result -> {
				ResponseJobDto job = result.getResponseBody();
				assertNotNull(job);
				assertEquals(origJob.getTitle(), job.getTitle());
			});

		// Edit the job with the stale job that doesn't have a resume id,
		//   but this time attempt to upload a resume
		editJobRequestUsingStaleJob(
			"018ead6b-d160-772d-a001-2606322ebd1c",
			TestInputs.testNewOrEditJobWithFiles
		);
		// Ensure that the job hasn't been changed (check the resumeId)
		getJobRequestReturningBodySpec("018ead6b-d160-772d-a001-2606322ebd1c")
			.consumeWith(result -> {
				ResponseJobDto job = result.getResponseBody();
				assertNotNull(job);
				assertEquals(
					origJob.getResumeId(),
					job.getResumeId());
			});
	}

	// Job edited has files, which are edited
	@Test
	@Order(22)
	void editJobWithFilesEditedThenGetThatJob() {
		// First, get the job to be edited to obtain its file ids
		ResponseJobDto origJob = getJobRequestReturningBodySpec(
			"018ead6b-d160-772d-a001-2606322ebd1c")
			.returnResult()
			.getResponseBody();

		assertNotNull(origJob);

		// Edit the job
		ResponseJobDto job = editJobRequestReturningJob(
			"018ead6b-d160-772d-a001-2606322ebd1c",
			TestInputs.testEditJobWithFilesEdited(
				origJob.getResumeId().toString(),
				origJob.getCoverLetterId().toString()
			)
		);

		assertNotNull(job);
		assertEquals("test swe with files edited", job.getTitle());
		// Ensure files are edited with the correct data
		assertEquals(job.getResumeId(), job.getResume().getId());
		assertEquals(job.getCoverLetterId(), job.getCoverLetter().getId());
		assertEquals("My_Edited_Test_Resume.docx", job.getResume().getFileName());
		assertEquals(
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
			job.getResume().getMimeType()
		);
		assertEquals("My_Edited_Test_CoverLetter.pdf", job.getCoverLetter().getFileName());
		assertEquals(
			"application/pdf",
			job.getCoverLetter().getMimeType()
		);

		// Get the job
		getJobRequestReturningBodySpec("018ead6b-d160-772d-a001-2606322ebd1c")
			.consumeWith(result -> {
				assertEquals(job, result.getResponseBody());
			});

		// TODO: Get the resume and cover letter to ensure they are edited
	}

	// ********* STALE JOB TEST 2 ********
	// Job has a cover letter, then edited to have a different cover letter id
	//   (by removing the cover letter, then adding one again). Edit job again,
	//   but using the old job w/ the old cover letter id
	// UPDATE: Using cover letter instead to check that resume changes are
	//   rolled back.
	@Test
	@Order(23)
	void editJobWithFilesUsingStaleJobWithOutdatedCoverLetterId() {
		// First, get the job to be edited to obtain its file ids
		ResponseJobDto origJob = getJobRequestReturningBodySpec(
			"018ead6b-d160-772d-a001-2606322ebd1c")
			.returnResult()
			.getResponseBody();

		assertNotNull(origJob);

		editJobRequestUsingStaleJob(
			"018ead6b-d160-772d-a001-2606322ebd1c",
			// Note: The previous test used TestInputs.testEditJobWithFilesEdited
			//   with origJob's cover letter id. I'm using a different id here
			//   to simulate the cover letter being outdated (different/out of
			//   sync).
			//   Notice how it's the resume that's edited, but the cover letter
			//     id is the stale one.
			//   This allows us to check that the resume changes are indeed
			//     rolled back or not even applied in the first place
			TestInputs.testEditJobWithResumeEdited(
				origJob.getResumeId().toString(),
				"ccccdead-d160-beef-a001-2606322e1234"
			)
		);

		// Ensure that the job hasn't been changed (check the resumeName)
		getJobRequestReturningBodySpec("018ead6b-d160-772d-a001-2606322ebd1c")
			.consumeWith(result -> {
				ResponseJobDto job = result.getResponseBody();
				assertNotNull(job);
				assertNotEquals(
					"mYRESU_mE_isALLc_orrUPTed.docx",
					job.getResume().getFileName()
				);
				assertEquals(
					origJob.getResume().getFileName(),
					job.getResume().getFileName()
				);
			});
	}

	// Here, I'm editing the job (clicking submit) even though I didn't really
	//   make any changes
	@Test
	@Order(24)
	void editJobWithFilesNotEditedThenGetThatJob() {
		// First, get the job to be edited to obtain its file ids
		ResponseJobDto origJob = getJobRequestReturningBodySpec(
			"018ead6b-d160-772d-a001-2606322ebd1c")
			.returnResult()
			.getResponseBody();

		assertNotNull(origJob);

		// Edit the job
		ResponseJobDto job = editJobRequestReturningJob(
			"018ead6b-d160-772d-a001-2606322ebd1c",
			TestInputs.testEditJobWithFilesNotEdited(
				origJob.getResumeId().toString(),
				origJob.getCoverLetterId().toString()
			)
		);

		assertNotNull(job);
		assertEquals("test swe with files edited", job.getTitle());
		// Ensure files are edited with the correct data
		assertEquals(job.getResumeId(), job.getResume().getId());
		assertEquals(job.getCoverLetterId(), job.getCoverLetter().getId());
		assertEquals("My_Edited_Test_Resume.docx", job.getResume().getFileName());
		assertEquals(
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
			job.getResume().getMimeType()
		);
		assertEquals("My_Edited_Test_CoverLetter.pdf", job.getCoverLetter().getFileName());
		assertEquals(
			"application/pdf",
			job.getCoverLetter().getMimeType()
		);

		// Get the job
		getJobRequestReturningBodySpec("018ead6b-d160-772d-a001-2606322ebd1c")
			.consumeWith(result -> {
				assertEquals(job, result.getResponseBody());
			});

		// TODO: Get the resume and cover letter to ensure they are NOT EDITED
	}

	// Same as editJobWithFilesNotEditedThenGetThatJob, but keepResume and
	//   keepCoverLetter are null (which shouldn't happen in the real app)
	@Test
	@Order(25)
	void editJobWithFilesNotEditedThenGetThatJobSanityCheck() {
		// First, get the job to be edited to obtain its file ids
		ResponseJobDto origJob = getJobRequestReturningBodySpec(
			"018ead6b-d160-772d-a001-2606322ebd1c")
			.returnResult()
			.getResponseBody();

		assertNotNull(origJob);

		// Edit the job
		ResponseJobDto job = editJobRequestReturningJob(
			"018ead6b-d160-772d-a001-2606322ebd1c",
			TestInputs.testEditJobWithFilesNotEditedSanityCheck(
				origJob.getResumeId().toString(),
				origJob.getCoverLetterId().toString()
			)
		);

		assertNotNull(job);
		assertEquals("test swe with files edited", job.getTitle());
		// Ensure files are edited with the correct data
		assertEquals(job.getResumeId(), job.getResume().getId());
		assertEquals(job.getCoverLetterId(), job.getCoverLetter().getId());
		assertEquals("My_Edited_Test_Resume.docx", job.getResume().getFileName());
		assertEquals(
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
			job.getResume().getMimeType()
		);
		assertEquals("My_Edited_Test_CoverLetter.pdf", job.getCoverLetter().getFileName());
		assertEquals(
			"application/pdf",
			job.getCoverLetter().getMimeType()
		);

		// Get the job
		getJobRequestReturningBodySpec("018ead6b-d160-772d-a001-2606322ebd1c")
			.consumeWith(result -> {
				assertEquals(job, result.getResponseBody());
			});

		// TODO: Get the resume and cover letter to ensure they are NOT EDITED
	}

	// NOTE: This includes STALE JOB TEST 3
	@Test
	@Order(26)
	void editJobWithFilesDeletedThenGetThatJob() {
		// First, get the job to be edited to obtain its file ids
		ResponseJobDto origJob = getJobRequestReturningBodySpec(
			"018ead6b-d160-772d-a001-2606322ebd1c")
			.returnResult()
			.getResponseBody();

		assertNotNull(origJob);

		// Edit the job
		ResponseJobDto job = editJobRequestReturningJob(
			"018ead6b-d160-772d-a001-2606322ebd1c",
			TestInputs.testEditJobWithFilesDeleted(
				origJob.getResumeId().toString(),
				origJob.getCoverLetterId().toString()
			)
		);

		assertNotNull(job);
		assertEquals("test swe with files edited", job.getTitle());
		// Ensure files are deleted
		assertNull(job.getResumeId());
		assertNull(job.getResume());
		assertNull(job.getCoverLetterId());
		assertNull(job.getCoverLetter());

		// Get the job
		ResponseJobDto res = getJobRequestReturningBodySpec(
			"018ead6b-d160-772d-a001-2606322ebd1c"
		)
			.returnResult()
			.getResponseBody();

		assertNotNull(res);
		// The native query used by getJob(get one job) returns a file
		//   with empty fields if the job doesn't that file. Need to
		//   set those to null so comparison could work properly
		res.nullifyEmptyFiles();
		assertEquals(job, res);

		// TODO: Get the resume and cover letter to ensure they are deleted
		//   ...

		// ******** STALE JOB TEST 3 ********
		// Doing this test here instead since I have no way of getting the
		//   resume + cover letter id of the job before the files were deleted
		editJobRequestUsingStaleJob(
			"018ead6b-d160-772d-a001-2606322ebd1c",
			// This is using the file ids from origJob, which was before
			//   the files were deleted
			TestInputs.testEditJobWithFilesEdited(
				origJob.getResumeId().toString(),
				origJob.getCoverLetterId().toString()
			)
		);

		// Ensure that the job hasn't been changed (check the file ids
		//   and make sure they're not there)
		getJobRequestReturningBodySpec("018ead6b-d160-772d-a001-2606322ebd1c")
			.consumeWith(result -> {
				ResponseJobDto res2 = result.getResponseBody();
				assertNotNull(res2);
				res2.nullifyEmptyFiles();
				assertEquals(origJob.getTitle(), res2.getTitle());
				assertNull(res2.getResumeId());
				assertNull(res2.getCoverLetterId());
				assertNull(res2.getResume());
				assertNull(res2.getCoverLetter());
			});
	}

	@Test
	@Order(27)
	void getOneResume() {
		// INSERT INTO resume(id, member_id, file_name, mime_type) VALUES
		//   ('323efefe-beef-e2a4-46c8-dadae1fedead', '269a3d55-4eee-4a2e-8c64-e1fe386b76f8', 'Example_Resume.pdf',
		//   'application/pdf');
		this.webTestClient
			.get()
			.uri("/resume/323efefe-beef-e2a4-46c8-dadae1fedead")
			// No resume and cover letter
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentType(MediaType.APPLICATION_JSON)
			.expectBody(ResponseResumeDto.class)
			.consumeWith(result -> {
				ResponseResumeDto resume = result.getResponseBody();
				assertNotNull(resume);
				assertEquals("Example_Resume.pdf", resume.getFileName());
				assertEquals("application/pdf", resume.getMimeType());
				double[] arr = {2, 4, 7, 10, 14};
				assertThat(Arrays.equals(arr, resume.getByteArrayAsArray())).isTrue();
			})
		;
	}

	@Test
	@Order(28)
	void getNonExistentResume() {
		this.webTestClient
			.get()
			.uri("/resume/d160dead-a001-a001-a001-fefe322ec1db")
			.exchange()
			.expectStatus()
			.isNotFound()
			.expectBody()
			.isEmpty()
		;
	}

	@Test
	@Order(29)
	void getOneCoverLetter() {
		// INSERT INTO cover_letter(id, member_id, file_name, mime_type) VALUES
		//   ('fefeefef-dada-e2a4-2bbb-3cccbeef32e3', '269a3d55-4eee-4a2e-8c64-e1fe386b76f8',
		//   'Example_CoverLetter.pdf', 'application/pdf');
		this.webTestClient
			.get()
			.uri("/coverLetter/fefeefef-dada-e2a4-2bbb-3cccbeef32e3")
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentType(MediaType.APPLICATION_JSON)
			.expectBody(ResponseCoverLetterDto.class)
			.consumeWith(result -> {
				ResponseCoverLetterDto coverLetter = result.getResponseBody();
				assertNotNull(coverLetter);
				assertEquals("Example_CoverLetter.pdf", coverLetter.getFileName());
				assertEquals("application/pdf", coverLetter.getMimeType());
				double[] arr = {2, 4, 7, 10, 14};
				assertThat(Arrays.equals(arr, coverLetter.getByteArrayAsArray())).isTrue();
			})
		;
	}

	@Test
	@Order(30)
	void getNonExistentCoverLetter() {
		this.webTestClient
			.get()
			.uri("/coverLetter/beefdead-d160-fefe-061d-dead1bf0beef")
			.exchange()
			.expectStatus()
			.isNotFound()
			.expectBody()
			.isEmpty()
		;
	}





	// ************************************************************************
	// ************************************************************************
	// ************************************************************************
	//
	//               AUTHENTICATION/AUTHORIZATION TESTS
	//
	// ************************************************************************
	// ************************************************************************
	// ************************************************************************
	// - TODO: Ideally, it's better to move these in a separate file

	@Test
	@Order(31)
	void loginWithExistentUser() {
		ResponseUserDto user = this.webTestClient
			.post()
			.uri("/user/login")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(TestAuthInputs.testExistentCredentials)
			.header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentType(MediaType.APPLICATION_JSON)
			.expectBody(ResponseUserDto.class)
			.returnResult()
			.getResponseBody()
		;

		assertNotNull(user);
		assertEquals(
			UUID.fromString("269a3d55-4eee-4a2e-8c64-e1fe386b76f8"),
			user.getId()
		);
		assertEquals("molly@books.com", user.getEmail());
		assertEquals("Molly Member", user.getName());
		assertEquals("member", user.getRoles()[0]);
		assertNotNull(user.getAccessToken());
	}

	@Test
	@Order(32)
	void loginWithExistentUser2() {
		this.webTestClient
			.post()
			.uri("/user/login")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(TestAuthInputs.testExistentCredentials2)
			.header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.id").isEqualTo("4604289c-b8fe-4560-8960-4da47fdfef94")
			.jsonPath("$.email").isEqualTo("anna@books.com")
		;
	}

	// Note: I'm returning a 404 when the wrong password is provided to not
	//   give out info about the existence of a user
	@Test
	@Order(33)
	void loginWithWrongPassword() {
		this.webTestClient
			.post()
			.uri("/user/login")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(TestAuthInputs.testWrongCredentials)
			.header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.exchange()
			.expectStatus()
			.isNotFound()
			.expectBody()
			.isEmpty()
		;
	}

	@Test
	@Order(34)
	void loginWithNonExistentUser() {
		this.webTestClient
			.post()
			.uri("/user/login")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(TestAuthInputs.testNonExistentUser)
			.header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.exchange()
			.expectStatus()
			.isNotFound()
			.expectBody()
			.isEmpty()
		;
	}

	@Test
	@Order(35)
	void signupWithNewUserThenLogin() {
		ResponseUserDto user = this.webTestClient
			.post()
			.uri("/user/signup")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(TestAuthInputs.testNewUser)
			.header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentType(MediaType.APPLICATION_JSON)
			.expectBody(ResponseUserDto.class)
			.returnResult()
			.getResponseBody()
		;

		assertNotNull(user);
		assertNotNull(user.getId());
		assertEquals("kevindurant@books.com", user.getEmail());
		assertEquals("Kevin Durant", user.getName());
		assertEquals("member", user.getRoles()[0]);
		assertNull(user.getAccessToken()); // Should be no access token

		// Login with newly created user
		this.webTestClient
			.post()
			.uri("/user/login")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(TestAuthInputs.testNewlyCreatedUser)
			.header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentType(MediaType.APPLICATION_JSON)
			.expectBody(ResponseUserDto.class)
			.consumeWith(result -> {
				// Can't use since no access token for signup return
//				assertEquals(user, result.getResponseBody());
				ResponseUserDto res = result.getResponseBody();
				assertNotNull(res);
				assertEquals(user.getId(), res.getId());
				assertEquals(user.getEmail(), res.getEmail());
				assertEquals(user.getName(), res.getName());
				assertThat(Arrays.equals(user.getRoles(), res.getRoles())).isTrue();
				assertNotNull(res.getAccessToken());
			})
		;
	}

	@Test
	@Order(36)
	void signupWithExistingUser() {
		this.webTestClient
			.post()
			.uri("/user/signup")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(TestAuthInputs.testNewUser)
			.header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.exchange()
			.expectStatus()
			.isEqualTo(HttpStatus.CONFLICT)
			.expectBody()
			.isEmpty()
		;
	}

	// TODO: *************** IMPORTANT ******************
	//  - Edit job w/ files, but with the fields out of order

}

// NOTES: 6/15/25
// - Search "how to validate request body in spring boot"
//   -- "how to validate parameters spring boot"
//   -- https://www.baeldung.com/spring-boot-bean-validation
//      + @Valid on request body
//      + When the target argument fails to pass the validation, Spring Boot throws a MethodArgumentNotValidException exception
//   -- https://stackoverflow.com/questions/64517537/springboot-validate-requestbody
//      + Add spring-boot-starter-validation dependency
//   -- https://medium.com/@tericcabrel/validate-request-body-and-parameter-in-spring-boot-53ca77f97fe9
//      + @Validated on controller
//   -- https://www.baeldung.com/java-bean-validation-not-null-empty-blank
//      + Difference between notnull, notblank, notempty
//   -- https://hibernate.org/validator/documentation/
//      + Refer to if needed
// - NOTE: I can't have @NotNull on id for example, since RequestJobDto
//   is used for both creating (no id) and editing (has id) jobs
//   -- So I'll need to check this manually

// NOTE: (5/10/25) What I did for container setup
// 1.) Using @TestPropertySource, specify that this test suite will use
//     application-product-integrationtest.properties
//     - In that properties file, set spring.jpa.hibernate.ddl-auto=none,
//       spring.datasource.username, spring.datasource.password.
//     - The rest will be set using @DynamicProperty sources
// 2.) Create the container using PostgreSQL Container with the db name, user
//     name, and password as specified in the env variables.
//     - Don't forget to run the script to initialize the database schema and data
//     - @Value doesn't work. I remember that the app context is created after
//       container creation (NOT SURE)
// 3.) Using @DynamicPropertySource, set properties (such as spring.datasource.url)
//     - Note that the container uses a random port, which is why we need to use
//       @DynamicPropertySource to set spring.datasource.url, which includes
//       the port
// 	   - Now, the app context will use these properties.
// 4.) Now we can test


// ---------------- NOTES ---------------
//
// https://spring.io/guides/gs/testing-web
// - contextLoads: test that will fail if the application context cannot start
// - webEnvironment=RANDOM_PORT to start the server with a random port (useful to avoid conflicts in test environments)
//   -- injection of the port with @LocalServerPort
// - Spring Boot has automatically provided a TestRestTemplate for you. All you have to do is add @Autowired to it.
//
//
// https://www.baeldung.com/spring-boot-testing
// - The integration tests need to start up a container to execute the test cases
//   -- Hence, some additional setup is required for this — all of this is easy in Spring Boot
// - The @SpringBootTest annotation is useful when we need to bootstrap the entire container.
//   The annotation works by creating the ApplicationContext that will be utilized in our tests
// - webEnvironment = SpringBootTest.WebEnvironment.MOCK,
//   -- we’re using WebEnvironment.MOCK here so that the container will operate in a mock servlet environment
//   -- ME: I'm using RANDOM_PORT since I don't want the container to run in a mock environment
// - the @TestPropertySource annotation helps configure the locations of properties files specific to our tests.
//   Note that the property file loaded with @TestPropertySource will override the existing application-product-integrationtest.properties file.
//   The application-product-integrationtest.properties contains the details to configure the persistence storage
//   NOTE: (5/5/25) ME: I can just copy the application-product-integrationtest.properties file I have, then change
//    the DB name and URL. Though, I'd also need to dynamically change what the test
//    database uses (In SlugSell, we set process.env.POSTGRES_DB = "test", since the test
//    database uses POSTGRES_DB).
//    - HOWEVER, the process could be different when using Test Containers
// - a test annotated with @SpringBootTest will bootstrap the full application context,
//   which means we can @Autowire any bean that’s picked up by component scanning into our test
//   -- Ex. services, controllers, etc.
// - @TestConfiguration annotation: Use a special test configuration, such as when we want
//   to avoid bootstrapping the real application context
//   -- we can create a separate test configuration class
//   -- Configuration classes annotated with @TestConfiguration are excluded from component scanning.
//      Therefore, we need to import it explicitly in every test where we want to @Autowire it.
//      We can do that with the @Import annotation
// - @ExtendWith(SpringExtension.class) provides a bridge between Spring Boot test features and
//   JUnit. Whenever we are using any Spring Boot testing features in our JUnit tests,
//   this annotation will be required.
// - ME: I can use Mockito to mock AWS calls.
//
//
// https://medium.com/@mbanaee61/api-testing-in-spring-boot-2a6d69e5c3ce
// - @Sql("/data.sql") // Optional: Initialize test data using SQL scripts
//  -- ME: They put this on top of each test method
//     Can I put this at a class level (for the whole test)?
//     Might not need this since the yaml file I'm using has references to data.sql, schema.sql, etc.
//
// https://rieckpil.de/spring-boot-testing-mockmvc-vs-webtestclient-vs-testresttemplate/
// - Both WebTestClient and TestRestTemplate are valid options for blocking integration tests
//   -- But WebTestClient can work for non-blocking apps (which is what it's used for) and
//      with mocked-servlet environments (MockMvc) and and for integration tests against
//      a running servlet container
//   -- IMPORTANT: We enrich this Spring Boot application with the Starter for Spring WebFlux.
//      However, we won’t mix non-blocking and blocking controller endpoints and stick to the blocking Tomcat servlet container.
//      The idea behind this additional dependency (Spring WebFlux) is to get access to the WebClient and the WebTestClient.
//   -- ME: See https://docs.spring.io/spring-framework/reference/testing/webtestclient.html
//      + WebTestClient can be used to perform end-to-end HTTP tests. It can also be used to test Spring MVC and Spring WebFlux applications without a running server via mock server request and response objects
//
//
// https://rieckpil.de/spring-webtestclient-for-efficient-testing-of-your-rest-api/
// - When using both the Spring Boot Starter Web and WebFlux, Spring Boot assumes we want a blocking servlet stack and auto-configures the embedded Tomcat for us\
// - https://rieckpil.de/guide-to-springboottest-for-spring-boot-integration-tests/
// - Spring Boot autoconfigures a WebTestClient bean for us, once our test uses: @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// - The client is already autoconfigured, and the base URL points to our locally running Spring Boot. There’s no need to inject the random port and configure the client
//
//
// https://www.baeldung.com/docker-test-containers
// - In this tutorial, we’ll be looking at the Java Testcontainers library. It allows us to use Docker containers within our tests.
// - Need to add testcontainers dependency
// - The rule is annotated with @ClassRule. As a result, it will start the Docker container before any test in that class runs.
//   The container will be destroyed after all methods are executed.
// - If you apply @Rule annotation, the GenericContainer rule will start a new container for each test method.
//   And it will stop the container when that test method finishes.
// - For example, we fire up a PostgreSQL container with PostgreSQLContainer rule
//   -- NOTE: (5/6/25) (From ChatGPT) In JUnit 5, the @Container annotation is used for lifecycle management.
//      Static fields annotated with @Container will start and stop the container once for the entire
//      test instance, while instance fields will start and stop the container before and after each test method
//   -- It is also possible to run PostgreSQL as a generic container. But it’d be more difficult to configure the connection
// - If the tests require more complex services, we can specify them in a docker-compose file
//   -- NOTE: (5/6/25) ME: How do I integrate my repository layer to this container?
//      + FIRST, this is what happens when not using TestContainers
//      + Annotating the test file with @SpringBootTest creates the application context
//        using what was specified in the application-product-integrationtest.properties file (the test version)
//      + When starting an application normally, this connects to the Postgres docker container
//        * Though, it doesn't start the container (which is why we need to manually do docker-compose up -d)
//      + And it actually wouldn't be this container using testcontainers, but the one started using docker-compose up -d
//      + The same thing should happen when running the test
//        * HOWEVER, wouldn't we need to manually change the environment variable before calling docker-compose up -d ???
//        * YES, this is what we do in CSE 187 SlugSell, where we programmatically change
//          process.env.POSTGRES_DB to test in db.ts for the test folder
//          ** We can simply achieve this by through the test application-product-integrationtest.properties file
//        * BUT...it's tedious to have to manually start a container for each test suite (Ex. product, resume, etc.)
//        * So I'll need to have some script that automatically starts up a container when running each test suite
//        * WHICH IS WHERE TESTCONTAINERS COME TO THE RESCUE
//          ** But again, I'm not sure how to tell the repository to use this created container
//          ** Maybe, I can simply just not start it manually myself and let testcontainers handle it?
// - Then, we use DockerComposeContainer rule. This rule will start and run services as defined in the compose file.
//   -- We use getServiceHost and getServicePost methods to build connection address to the service
//   NOTE: (5/6/25) ME: If the application context for the tests are created after this container has
//    been started, the datasource, db url, etc. specified in the test application-product-integrationtest.properties would
//    refer to the container created here
//
//
// https://www.baeldung.com/spring-boot-testcontainers-integration-test
// - To start using the PostgreSQL instance in a single test class, we have to create a container definition first and then use its parameters to establish a connection
// - This one has an example on how to set up a container with a certain db name, user , password, etc.
// - In addition to the JUnit 4 rules approach, we can modify the JDBC URL and instruct the Testcontainers to create a database instance per test class.
//   This approach will work without requiring us to write some infrastructural code in our tests.
//   -- spring.datasource.url=jdbc:tc:postgresql:11.1:///integration-tests-db
//   -- The “tc:” will make Testcontainers instantiate database instances without any code change
//   -- NOTE: (5/6/25) ME: jdbc:tc:postgresql://localhost:5432/dev
//      * I'll probably use this
//      * NOTE: (5/6/25) NOTE: Replace dev with test if I'm using test as the POSTGRES_DB value
// - System.setProperty() is how we set environment variables
// - As in previous examples, we applied the @ClassRule annotation to a field holding the container definition.
//   This way, the DataSource connection properties are populated with correct values before Spring context creation
//
//
// https://dev.to/mspilari/integration-tests-on-spring-boot-with-postgresql-and-testcontainers-4dpc
// - @Testcontainers
//   -- This annotation is from the Testcontainers library and is used to manage container lifecycles automatically during the test lifecycle.
//   -- It ensures that containers are started before any tests run and stopped when tests complete.
// - @Container
//   -- A Testcontainers-specific annotation that designates a field as a container, making sure the container starts before running tests and stops afterward.
//   -- In this case, it's used to manage a PostgreSQL container that emulates a database environment for testing
// - @ServiceConnection
//   -- This annotation is part of Spring Boot’s integration with Testcontainers. It allows automatic service discovery,
//      helping Spring Boot use the PostgreSQLContainer to connect to the
//      PostgreSQL instance.
//
//
// https://rieckpil.de/howto-write-spring-boot-integration-tests-with-a-real-database/
// - For projects where we have multiple dependencies from Testcontainers, we must align their
//   versions to avoid incompatibilities. Testcontainers provides a Maven Bill of Materials (BOM)
//   for this purpose. Once we define the testcontainers-bom as part of Maven’s dependencyManagement
//   section, we can include all Testcontainers dependencies without specifying their versions.
//   The BOM will align all Testcontainers dependency versions for us
// - NOTE: (5/6/25) This one has a way to add dependencies using Gradle
// - Using JUnit Jupiter, we can register the Testcontainers extension with @Testcontainers.
//   Next, we have to identify all our container definitions with @Container
// - .withInitScript("config/INIT.sql")
//   -- ME: This could be a way to fill the db
//- Testcontainers maps the PostgreSQL’s main port (5432) to a random and ephemeral port,
//  so we must override our configuration dynamically.
//  For Spring Boot applications < 2.2.6, we can achieve this with an
//  ApplicationContextInitializer and set the connection parameters dynamically
// NOTE: (5/6/25) For applications that use JUnit Jupiter (part of JUnit 5),
//  we can’t use the @ClassRule anymore.
//  The extension model of JUnit Jupiter exceeds the rule/runner API from JUnit 4.
//  With the help of @DynamicPropertySource we can dynamically override the datasource connection parameters
// NOTE: (5/6/25) Simplified Spring Boot Configuration with @ServiceConnection
//  Using: JUnit 5 and Spring Boot >= 3.1
//  Traditionally, when using Testcontainers for integration testing, we would define a
//  @DynamicPropertySource to configure the application properties with container-specific
//  details dynamically. With Spring Boot 3.1, we can skip this step by annotating our container
//  fields with @ServiceConnection
//
//
// https://testcontainers.com/guides/testing-spring-boot-rest-api-using-testcontainers/
// - Has start() for @BeforeAll
//
// https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html
//- If you are using JUnit 5, there is no need to add the equivalent
//  @ExtendWith(SpringExtension.class) as @SpringBootTest and the other @Test annotations
//  are already annotated with it
//- The @LocalServerPort annotation can be used to inject the actual port used into your test.
//  For convenience, tests that need to make REST calls to the started server can additionally
//  autowire a WebTestClient
//  -- This setup requires spring-webflux on the classpath
//
//
// https://docs.spring.io/spring-boot/reference/testing/testcontainers.html
// - @Testcontainers, @Container
// - Spring Boot’s auto-configuration can consume the details of a service connection and use
//   them to establish a connection to a remote service.
//   When doing so, the connection details take precedence over any connection-related configuration properties.
//   -- @ServiceConnection annotation on the field annotated with @Container
//   -- You’ll need to add the spring-boot-testcontainers module as a test dependency in order to use service connections with Testcontainers.
// - By default all applicable connection details beans will be created for a given Container.
//   For example, a PostgreSQLContainer will create both JdbcConnectionDetails and R2dbcConnectionDetails
// - A slightly more verbose but also more flexible alternative to service connections is @DynamicPropertySource.
//   A static @DynamicPropertySource method allows adding dynamic property values to the Spring Environment


// ------------ Main Resources Used -------------
// https://spring.io/guides/gs/testing-web
// https://www.baeldung.com/spring-boot-testing
// https://medium.com/@mbanaee61/api-testing-in-spring-boot-2a6d69e5c3ce
//
// Main sources used for actual tests:
// https://rieckpil.de/spring-boot-testing-mockmvc-vs-webtestclient-vs-testresttemplate/
// https://rieckpil.de/spring-webtestclient-for-efficient-testing-of-your-rest-api/
//
// https://www.baeldung.com/docker-test-containers
// https://www.baeldung.com/spring-boot-testcontainers-integration-test
// https://dev.to/mspilari/integration-tests-on-spring-boot-with-postgresql-and-testcontainers-4dpc
// https://rieckpil.de/howto-write-spring-boot-integration-tests-with-a-real-database/
// https://testcontainers.com/guides/testing-spring-boot-rest-api-using-testcontainers/
// Marco Behler JUnit 5 Tutorial: https://www.youtube.com/watch?v=6uSnF6IuWIw




// -------------- Other useful resources -------------------

// TestRestTemplate vs. MockMvc vs RestAssured
// - https://stackoverflow.com/questions/52051570/whats-the-difference-between-mockmvc-restassured-and-testresttemplate
//   -- Seems like MockMvc only mocks the service and other layers
//      + Primarily for unit testing
//   -- TestRestTemplate and RestAssured are for integration testing
//   -- https://medium.com/swlh/https-medium-com-jet-cabral-testing-spring-boot-restful-apis-b84ea031973d
//      + Seems good
// - https://stackoverflow.com/questions/46732371/why-are-there-different-types-of-integration-tests-in-spring-boot
//   -- Also a good read
// - https://rieckpil.de/spring-boot-testing-mockmvc-vs-webtestclient-vs-testresttemplate/
//   -- The table is very helpful
//   -- Good example on how to use TestRestTemplate TODO: (5/3/25) I can use this example

// RestAssured
// - I keep seeing this one too
// - https://www.baeldung.com/rest-assured-tutorial

// OpenAPI specification:
// https://www.baeldung.com/spring-rest-openapi-documentation
// https://github.com/springdoc/springdoc-openapi

// Postman:
// https://medium.com/turkcell/spring-boot-rest-api-testing-with-postman-bb283b124416

// Swagger/OpenAPI vs Postman
// https://www.reddit.com/r/explainlikeimfive/comments/mtwi2r/eli5_software_development_what_is_the_difference/

// Controller tests, integration tests, and unit tests
// https://www.reddit.com/r/SpringBoot/comments/fd1qbu/controller_unit_tests_vs_integration_tests_in/
// https://www.reddit.com/r/rails/comments/iab5w3/what_is_the_difference_about_a_controller_test/
// https://www.reddit.com/r/node/comments/xhe6kj/how_do_you_guys_deal_with_unit_testing_against_a/

// TODO: (Later) Should AWS calls be mocked in integration tests?
// - https://www.reddit.com/r/aws/comments/lyano4/integration_testing_aws_services/
