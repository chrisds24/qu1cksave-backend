package com.qu1cksave.qu1cksave_backend.job;

import com.qu1cksave.qu1cksave_backend.coverletter.CoverLetter;
import com.qu1cksave.qu1cksave_backend.coverletter.CoverLetterMapper;
import com.qu1cksave.qu1cksave_backend.coverletter.CoverLetterRepository;
import com.qu1cksave.qu1cksave_backend.coverletter.ResponseCoverLetterDto;
import com.qu1cksave.qu1cksave_backend.exceptions.SQLAddFailedException;
import com.qu1cksave.qu1cksave_backend.exceptions.SQLDeleteFailedException;
import com.qu1cksave.qu1cksave_backend.exceptions.SQLEditFailedException;
import com.qu1cksave.qu1cksave_backend.exceptions.SQLGetFailedException;
import com.qu1cksave.qu1cksave_backend.exceptions.SQLNotFoundException;
import com.qu1cksave.qu1cksave_backend.exceptions.StaleFrontendJobException;
import com.qu1cksave.qu1cksave_backend.resume.ResponseResumeDto;
import com.qu1cksave.qu1cksave_backend.resume.Resume;
import com.qu1cksave.qu1cksave_backend.resume.ResumeMapper;
import com.qu1cksave.qu1cksave_backend.resume.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class JobService {
    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final CoverLetterRepository coverLetterRepository;

    // If I need a property value, Ex:
    //   @Value("${postgres.host}") String postgresHost
    public JobService(
        @Autowired JobRepository jobRepository,
        @Autowired ResumeRepository resumeRepository,
        @Autowired CoverLetterRepository coverLetterRepository
    ) {
        this.jobRepository = jobRepository;
        this.resumeRepository = resumeRepository;
        this.coverLetterRepository = coverLetterRepository;
    }

    // Note: https://www.marcobehler.com/guides/spring-transaction-management-transactional-in-depth
    // - I'll need a JpaTransactionManager to use Transactional with Hibernate
    // https://stackoverflow.com/questions/10394857/how-to-use-transactional-with-spring-data
    // - Talks about @Transactional(readOnly = true) for select queries
    // https://docs.spring.io/spring-data/jpa/reference/jpa/transactions.html
    // - Talks about how having Transactional here causes the transaction configurations
    //   at the repositories to be neglected (which is what we want) and that
    //   the outer transaction configuration determines the actual one used.
    //   -- Marco Behler also mentioned this
    @Transactional(readOnly = true)
    public List<ResponseJobDto> getJobs(UUID userId) {
        try {
            // Saving this commented line for reference, where the repository
            //   returns a List<Job>, which gets converted to List<JobDto>
            //        return jobRepository.findByMemberIdWithFiles(userId).stream().map(JobMapper::toDto).collect(Collectors.toList());
            return jobRepository.findByMemberIdWithFiles(userId);
        } catch (RuntimeException err) {
            throw new SQLGetFailedException(
                "Select multiple jobs with files failed", err
            );
        }
    }

    @Transactional(readOnly = true)
    public ResponseJobDto getJob(UUID id, UUID userId) {
        try {
            // If this returns null, won't throw an exception. But controller
            //   should handle this to set status to 404 and return null
            return jobRepository.findByIdAndMemberId(id, userId).map(JobMapper::toResponseDto).orElse(null);
        } catch (RuntimeException err) {
            throw new SQLGetFailedException("Select one job failed", err);
        }
    }

    // @Modifying annotation
    // - https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html#jpa.modifying-queries
    // - Only relevant when using @Query
    //   -- So this should go to a function in the repo if I have a
    //      modifying @Query
    @Transactional
    public ResponseJobDto createJob(RequestJobDto newJob, UUID userId) {
        // 1: Create the resume (if there's one), returning the id (resumeId)
        // 2: Create the coverLetter, returning the id (coverLetterId)
        // 3: Create the job, using the associated files' id if they exists
        // 4: Add files to S3
        // At any point, if any of the steps above fail, the whole transaction
        //   should and will fail
        // 5: Return the job with file metadata included

        // --------------- 1.) Create the resume -------------------
        ResponseResumeDto responseResumeDto = null;
        if (newJob.getResume() != null) {
            Resume newResumeEntity = ResumeMapper.createEntity(
                newJob.getResume(), userId
            );
            try {
                responseResumeDto = ResumeMapper.toResponseDto(
                    resumeRepository.save(newResumeEntity)
                );
            } catch (RuntimeException err) {
                throw new SQLAddFailedException("Save new resume failed", err);
            }
        }

        // --------------- 2.) Create the cover letter -------------------
        ResponseCoverLetterDto responseCoverLetterDto = null;
        if (newJob.getCoverLetter() != null) {
            CoverLetter newCoverLetterEntity = CoverLetterMapper.createEntity(
                newJob.getCoverLetter(), userId
            );
            try {
                responseCoverLetterDto = CoverLetterMapper.toResponseDto(
                    coverLetterRepository.save(newCoverLetterEntity)
                );
            } catch (RuntimeException err) {
                throw new SQLAddFailedException(
                    "Save new cover letter failed", err
                );
            }
        }

        // --------------- 3.) Create the job -------------------

        // Creating new entity
        // - https://spring.io/guides/gs/accessin[[[[[[[[[[=g-data-jpa
        // - https://docs.spring.io/spring-data/jpa/reference/jpa/entity-persistence.html
        // Since we have just created a resume/cover letter, newJob won't have
        //   a resumeId or coverLetterId, so we need to set it to the id
        //   of the file metadata returned when we saved each file metadata
        Job newJobEntity = JobMapper.createEntity(newJob, userId);
        newJobEntity.setResumeId(
            responseResumeDto != null ? responseResumeDto.getId() : null
        );
        newJobEntity.setCoverLetterId(
            responseCoverLetterDto != null ?
                responseCoverLetterDto.getId() : null
        );

        // Don't forget to attach the file metadata
        ResponseJobDto responseJobDto;
        try {
            responseJobDto = JobMapper.toResponseDtoWithFiles(
                jobRepository.save(newJobEntity),
                responseResumeDto,
                responseCoverLetterDto
            );
        } catch (RuntimeException err) {
            throw new SQLAddFailedException("Save new job failed", err);
        }

        // --------------- TODO: 4.) Add files to S3 -------------------

        return responseJobDto;
    }

    @Transactional
    public ResponseJobDto editJob(UUID id, UUID userId, RequestJobDto editJob) {
        // 1.) Get job from database
        //     - We'll use the resumeId and coverLetterId from this to query
        //       the resume and cover letter tables since the frontend job
        //       could have stale data for those
        //     - TODO: (5/13/25) What's the most appropriate way to handle a
        //        situation like this? What are all the situations where there
        //        could be stale data?
        //        -- Ex. jobA in tab1 and tab2 in same state, has resume
        //        -- Edit in tab2 to not have resume
        //        -- Tab1 now has stale resume data
        //        -- Edit resume in tab1 (still has resumeId, which should no
        //           longer exists
        //        -- If we used the stale resumeId from tab1, we won't be able
        //           to update resume related data.
        //        Even worse is if a resume was added again in tab2, so now
        //        the job has a different resumeId instead of nothing
        //        -- If we save the job in tab1, it could update the resumeId
        //           to be the one in tab1, so we lose the actual resume file
        //           in S3 associated with the resumeId in tab2
        // 2.) Query resume table (add, edit, delete, or do nothing)
        // 3.) Query cover letter table
        // 4.) Edit job
        //     - We'll use the resume id that we get from querying the resume
        //       tables (Not the resumeId from jobEntity)
        //     - If we added, updated, or didn't modify an existing resume, we
        //       will have queried the resume table (even if we're keeping the
        //       same file just to keep the code logic simple) and would end
        //       up with a resume
        //     - If we deleted or didn't have a resume to begin with, resume
        //       would be null and we'd have no resume id
        //     - Same applies to cover letters
        // 5.) Make S3 calls for resume and cover letter
        // 6.) Return the job with resume and cover letter metadata attached

        // -------------------- 1.) Get the job -------------------------------
        Job jobEntity;
        try {
            jobEntity = jobRepository.findByIdAndMemberId(id, userId).orElse(null);
        } catch (RuntimeException err) {
            throw new SQLGetFailedException(
                "Select job failed when editing job", err
            );
        }

        // Don't really need to throw an exception since we haven't edited
        // anything in the transaction yet, but just do it for consistency
        if (jobEntity == null) { // Job not found
            throw new SQLNotFoundException("Job not found when editing job " +
                "even though it should exist"
            );
        }

        // Mismatch between resumeId from frontend job (editJob) and database
        //   job, so editJob is stale. So don't allow edit and return an
        //   error instead
        // if ((newJob.resume_id && !resumeId) || (newJob.cover_letter_id && !coverLetterId)) {
        // - Original only checks if stale frontend job has a file but the db
        //   job does not
        // - It's better to check if there's a mismatch
        // - What if frontend job didn't have a resume, but the db job does
        // - An edit is made using the stale job (aka stale browser tab) and
        //   user adds a resume
        // - The db creates a new row with new id for this resume and we also
        //   add to S3
        // - Now, the job has this new id, resulting in us losing a reference
        //   to the other resume id
        // - UPDATE: This won't happen. Even if the frontend job had a resume
        //   but no resume id, the db job's resume id (most up to date) is
        //   always used. Therefore, this will function as an "edit resume"
        //   which is what it actually is
        //   -- However, it's better to just not allow further operations
        //      until the user updates their frontend jobs by reloading
        // - NOTE: There is no issue with stale frontend jobs with no files
        //   The stale frontend job can just update the db job
        //   If the job doesn't actually exist since it's deleted, then
        //   there'll simply be a Not Found error
        if (!Objects.equals(editJob.getResumeId(), jobEntity.getResumeId()) ||
            !Objects.equals(editJob.getCoverLetterId(), jobEntity.getCoverLetterId())
        ) {
            throw new StaleFrontendJobException(
                "Editing stale frontend job. Either one of or both the " +
                    "resume/cover letter ids are inconsistent with the ones " +
                    "found in the database"
            );
        }

        // ---------------------- 2.) Query resume table ----------------------
        // The same ideas apply to dealing with cover letters
        // We use a combination of editJob having a resume, editJob having a
        //   resumeId, and keepResume being true or false
        //     NOTE: No editJob.resume means that no resume is being uploaded
        //       (DOES NOT MEAN that the actual job does not currently have
        //        a resume. That is what editJob.resumeId is for, which
        //        indicates that the actual job has a resume...unless the
        //        frontend data is stale...SEE ABOVE)
        // 1.) No editJob.resume, no editJob.resumeId (Case C.a in frontend):
        //     -- Job to be edited has no resume to begin with
        //     -- Nothing to attach to job
        // 2.) No editJob.resume, has editJob.resumeId, keepResume is true (Case A):
        //     -- Job has a resume, but we won't update or delete
        //     -- Attach resume when returning job
        // 3.) No editJob.resume, has editJob.resumeId, keepResume is false (Case C.b):
        //     -- Job has a resume, which we will delete.
        //     -- Nothing to attach to job
        // 4.) Has editJob.resume, but no editJob.resumeId (Case B.a):
        //     -- Job has no resume, but a resume has been uploaded which
        //        we are adding to the job
        //     -- Attach resume when returning job
        // 5.) Has editJob.resume and a editJob.resumeId (Case B.b):
        //     -- We're replacing the resume specified by editJob.resumeId to
        //        be editJob.resume (the currently uploaded resume)
        //     -- Attach resume when returning job
        // IMPORTANT: Keep in mind that we are using the resumeId from the
        //   retrieved job using findBy. I'm using editJob.resumeId here to
        //   mean that the user meant to edit a job with a resume id.
        //   They should be the same in most cases, except when the frontend has
        //   stale data where we simply terminate the request
        ResponseResumeDto resume = null;
        String resumeAction = null; // Used for S3 call
        UUID resumeId = jobEntity.getResumeId();

        // IMPORTANT: Notice how we're using jobEntity.resumeId, not
        //   editJob.resumeId (which could be stale)
        // Though, if we do get here, it wouldn't be stale (since the
        //   request is terminated if it is)
        if (editJob.getResume() == null) { // Cases 1, 2, and 3 (no uploaded resume)
            if (resumeId != null) { // Cases 2 and 3
                Boolean keepResume = editJob.getKeepResume();
                if (keepResume != null && keepResume) {
                    // Case 2: Keep the resume specified by editJob.resumeId
                    // - Need to get it so we can attach metadata later
                    try {
                        resume = resumeRepository.findByIdAndMemberId(
                            resumeId,
                            userId
                        ).map(ResumeMapper::toResponseDto).orElse(null);
                    } catch (RuntimeException err) {
                        throw new SQLGetFailedException(
                            "Select resume failed when editing job", err
                        );
                    }
                    if (resume == null) {
                        throw new SQLNotFoundException(
                            "Resume not found when editing job even though " +
                                "it should exist"
                        );
                    }
                    // --------- IMPORTANT: Original Node version --------
                    // - It throws an exception if the resume isn't found
                    //   -- Which is appropriate since the resume should exist
//                try {
//                    const { rows } = await pool.query(query);
//                    resume = rows[0];
//                } catch {
//                    return undefined;
//                }
                    // -------------------------------------------------------
                } else { // keepResume is false (or is not set, which won't happen)
                    // Case 3: Delete the resume specified by editJob.
                    // TODO: Should throw an exception so I can catch and return
                    //   null, which the frontend expects where there's an error
                    Integer count = 0;
                    try {
                        count = resumeRepository.deleteByIdAndMemberId(resumeId, userId);
                    } catch(RuntimeException err) {
                        throw new SQLDeleteFailedException(
                            "Delete resume failed when editing job", err
                        );
                    }
                    if (count < 1) {
                        // Original Node version doesn't do this
                        throw new SQLDeleteFailedException(
                            "Delete resume didn't delete anything during " +
                                "edit job, but it should have"
                        );
                    }
                    resumeAction = "delete";
                }
            } // else...Case 1: editJob has no resume id and no resume...So nothing to do
        } else { // Cases 4 and 5
            if (resumeId == null) {
                // Case 4: Add new resume
                Resume newResumeEntity = ResumeMapper.createEntity(
                    editJob.getResume(), userId
                );
                // TODO: If save somehow returns null, should cause an exception
                // TODO: Should throw an exception so I can catch and return
                //   null, which the frontend expects where there's an error
                try {
                    resume = ResumeMapper.toResponseDto(resumeRepository.save(newResumeEntity));
                } catch(RuntimeException err) {
                    throw new SQLAddFailedException(
                        "Save new resume failed during edit job", err
                    );
                }
                resumeAction = "put";
            } else {
                // Case 5: Update existing resume
                // Need to get resume entity first, update fields, then save
                Resume resumeEntity = null;
                try {
                    resumeEntity = resumeRepository.findByIdAndMemberId(
                        resumeId,
                        userId
                    ).orElse(null);
                } catch (RuntimeException err) {
                    throw new SQLGetFailedException(
                        "Select resume failed during edit job", err
                    );
                }
                if (resumeEntity == null) {
                    throw new SQLNotFoundException(
                        "Resume not found during edit job even though it " +
                            "should exist"
                    );
                } else {
                    resumeEntity.setFileName(editJob.getResume().getFileName());
                    resumeEntity.setMimeType(editJob.getResume().getMimeType());
                    try {
                        resume = ResumeMapper.toResponseDto(resumeRepository.save(resumeEntity));
                    } catch(RuntimeException err) {
                        throw new SQLEditFailedException(
                            "Save edited resume failed during edit job", err
                        );
                    }
                    resumeAction = "put";
                }
            }
        }

        // ------------------- 3.) Query cover letter table -------------------
        // Same strategy as querying the resume table
        ResponseCoverLetterDto coverLetter = null;
        String coverLetterAction = null; // Used for S3 call
        UUID coverLetterId = jobEntity.getCoverLetterId();

        if (editJob.getCoverLetter() == null) { // Cases 1, 2, and 3 (no uploaded cover letter)
            if (coverLetterId != null) { // Cases 2 and 3
                Boolean keepCoverLetter = editJob.getKeepCoverLetter();
                if (keepCoverLetter != null && keepCoverLetter) {
                    // Case 2: Keep the cover letter specified by editJob.coverLetterId
                    // - Need to get it so we can attach metadata later
                    try {
                        coverLetter = coverLetterRepository.findByIdAndMemberId(
                            coverLetterId,
                            userId
                        ).map(CoverLetterMapper::toResponseDto).orElse(null);
                    } catch(RuntimeException err) {
                        throw new SQLGetFailedException(
                            "Select cover letter failed in edit job", err
                        );
                    }
                    if (coverLetter == null) {
                        throw new SQLNotFoundException(
                            "Cover letter not found when editing job even " +
                                "though it should exist"
                        );
                    }
                } else { // keepCoverLetter is false (or is not set, which won't happen)
                    // Case 3: Delete the cover letter specified by editJob.
                    // TODO: Should throw an exception so I can catch and return
                    //   null, which the frontend expects where there's an error
                    Integer count = 0;
                    try {
                        count = coverLetterRepository.deleteByIdAndMemberId(coverLetterId, userId);
                    } catch(RuntimeException err) {
                        throw new SQLDeleteFailedException(
                            "Delete cover letter failed during edit job", err
                        );
                    }
                    if (count < 1) {
                        // Original Node version doesn't do this
                        throw new SQLDeleteFailedException(
                            "Delete cover letter didn't delete anything during " +
                                "edit job, but it should have"
                        );
                    }
                    coverLetterAction = "delete";
                }
            } // else...Case 1: editJob has no cover letter id and no cover letter...So nothing to do
        } else { // Cases 4 and 5
            if (coverLetterId == null) {
                // Case 4: Add cover Letter
                CoverLetter newCoverLetterEntity = CoverLetterMapper.createEntity(
                    editJob.getCoverLetter(), userId
                );
                // TODO: If save somehow returns null, should cause an exception
                // TODO: Should throw an exception so I can catch and return
                //   null, which the frontend expects where there's an error
                try {
                    coverLetter = CoverLetterMapper.toResponseDto(coverLetterRepository.save(newCoverLetterEntity));
                } catch(RuntimeException err) {
                    throw new SQLAddFailedException(
                        "Save new cover letter failed during edit job", err
                    );
                }
                coverLetterAction = "put";
            } else {
                // Case 5: Update existing cover letter
                // Need to get cover letter entity first, update fields, then save
                CoverLetter coverLetterEntity;
                try {
                    coverLetterEntity = coverLetterRepository.findByIdAndMemberId(
                        coverLetterId,
                        userId
                    ).orElse(null);
                } catch (RuntimeException err) {
                    throw new SQLGetFailedException(
                        "Select cover letter failed during edit job", err
                    );
                }

                if (coverLetterEntity == null) {
                    throw new SQLNotFoundException(
                        "Cover letter not found during edit job even though " +
                            "it should exist"
                    );
                } else {
                    coverLetterEntity.setFileName(editJob.getCoverLetter().getFileName());
                    coverLetterEntity.setMimeType(editJob.getCoverLetter().getMimeType());
                    try {
                        coverLetter = CoverLetterMapper.toResponseDto(coverLetterRepository.save(coverLetterEntity));
                    } catch (RuntimeException err) {
                        throw new SQLEditFailedException(
                            "Save edited cover letter failed during edit job", err
                        );
                    }
                    coverLetterAction = "put";
                }
            }
        }

        // --------------------------- 4.) Edit job ---------------------------
        // https://stackoverflow.com/questions/11881479/how-do-i-update-an-entity-using-spring-data-jpa
        // - JPA follows the latter approach. save() in Spring Data JPA is
        //   backed by merge() in plain JPA, therefore it makes your entity
        //   managed as described above. It means that calling save() on an
        //   object with predefined id will update the corresponding database
        //   record rather than insert a new one, and also explains why save()
        //   is not called create()
        // https://stackoverflow.com/questions/74847355/what-is-the-right-way-to-update-a-db-entry-in-spring-jpa
        // a.) Get from DB -> Update using setters -> save
        // b.) Native query
        // ----------------------------
        // Which one to choose?
        // a.) Spring Data JPA way, but requires two database queries
        //     - One to get the job, another one to save
        // b.) Faster since only requires one database query
        //     - However, we want to return the job later, so we'll need to
        //       get it since modifying queries can't return the modified
        //       object
        //     - I'm also getting the job in the first place (see #1), so I'm
        //       already doing the call to get to begin with
        // - I can just return the job from the get, since it'll have all the
        //   updates
        // https://www.baeldung.com/spring-data-partial-update
        // - @DynamicUpdate or updatable param for @Column
        // - https://dzone.com/articles/when-to-use-the-dynamicupdate-with-spring-data-jpa
        //   -- Good use case explanation
        //
        // Just editing all editable fields
        //   resumeId, coverLetterId, title, companyName, jobDescription, notes,
        //   isRemote, salaryMin, salaryMax, country, usState, city, dateApplied,
        //   datePosted, jobStatus, links, foundFrom
        // - Notice how there's no id, memberId, and dateSaved since those
        //   should not be updatable
        // - In the Node/Express version, I just edited even the values that
        //   stayed the same for simplicity
        //   -- I believe the data that weren't updated weren't set by the
        //      frontend, but they were set to null in the SQL query
        // Here, I'm just using the resume and cover letter id from editJob
        //   instead of jobEntity since they should be the same (if they were
        //   not, the request would have been terminated)
        jobEntity.setColumnsFromRequestJobDto(editJob);
        // Update resume id and cover letter id
        // Cases 2, 4, and 5 would have a resume
        // For Cases 2 (keep resume) and 5 (edit), we can still just update
        //   resumeId even though there's no actual change to it.
        // For Case 4 (add), we add a resumeId
        jobEntity.setResumeId(resume != null ? resume.getId() : null);
        jobEntity.setCoverLetterId(coverLetter != null ? coverLetter.getId(): null);
        ResponseJobDto responseJobDtoWithFiles;
        try {
            responseJobDtoWithFiles = JobMapper.toResponseDtoWithFiles(
                jobRepository.save(jobEntity),
                resume,
                coverLetter
            );
        } catch (RuntimeException err) {
            throw new SQLEditFailedException(
                "Save edited job failed during edit job", err
            );
        }

        // TODO: 5.) S3 Calls
        //  -

        // 6.) Return the job with resume and cover letter metadata attached
        return responseJobDtoWithFiles;
    }

    @Transactional
    public ResponseJobDto deleteJobByIdAndUserId(UUID id, UUID userId) {
        // 1.) Delete job
        // 2.) Delete resume from database (if any)
        // 3.) Delete cover letter from database (if any)
        // 4.) Delete resume from S3 (if any)
        // 5.) Delete cover letter from S3 (if any)
        // 6.) Return deleted job with files

        // ------------------ 1.) Delete job --------------------

        // Need to get job first, then delete so we can return the job later
        //   since modifying queries like delete can only return void, int, or
        //   Integer
//        ResponseJobDto job = jobRepository.findById(id).map(JobMapper::toResponseDto).orElse(null);
        Job jobEntity;
        try {
            jobEntity = jobRepository.findByIdAndMemberId(id, userId).orElse(null);
        } catch(RuntimeException err) {
            throw new SQLGetFailedException(
                "Select job failed during delete", err
            );
        }
        if (jobEntity == null) { // Job does not exist, even though it should
            throw new SQLNotFoundException(
                "Job not found during delete, even though it should exist"
            );
        }
        Integer count = 0;
        try {
            count = jobRepository.deleteByIdAndMemberId(id, userId);
        } catch(RuntimeException err) {
            throw new SQLDeleteFailedException(
                "Delete job failed during delete", err
            );
        }
        if (count < 1) {
            throw new SQLDeleteFailedException(
                "Delete didn't delete job during delete, even though it should"
            );
        }

        // 2.) ------------ Delete resume from database -----------------
        ResponseResumeDto resume = null;
        if (jobEntity.getResumeId() != null) { // If resume exists, delete it
            // First, find the resume so we can return it later
            try {
                resume = resumeRepository.findByIdAndMemberId(
                    jobEntity.getResumeId(), userId
                ).map(ResumeMapper::toResponseDto).orElse(null);
            } catch(RuntimeException err) {
                throw new SQLGetFailedException(
                    "Select resume failed during delete", err
                );
            }
            if (resume == null) { // resume does not exist, even though it should
                throw new SQLNotFoundException(
                    "Resume not found during delete, even though it should exist"
                );
            }

            count = 0;
            try {
                count = resumeRepository.deleteByIdAndMemberId(
                    jobEntity.getResumeId(), userId
                );
            } catch(RuntimeException err) {
                throw new SQLDeleteFailedException(
                    "Delete resume failed during delete", err
                );
            }
            if (count < 1) {
                throw new SQLDeleteFailedException(
                    "Delete didn't delete resume during delete, even though " +
                        "it should"
                );
            }
        }

        // 3.) ------------ Delete cover letter from database -----------------
        ResponseCoverLetterDto coverLetter = null;
        if (jobEntity.getCoverLetterId() != null) { // If cover letter exists, delete it
            // First, find the cover letter so we can return it later
            try {
                coverLetter = coverLetterRepository.findByIdAndMemberId(
                    jobEntity.getCoverLetterId(), userId
                ).map(CoverLetterMapper::toResponseDto).orElse(null);
            } catch(RuntimeException err) {
                throw new SQLGetFailedException(
                  "Select cover letter failed during delete"
                );
            }

            if (coverLetter == null) { // cover letter does not exist, even though it should
                throw new SQLNotFoundException(
                    "Cover letter not found during delete, even though it " +
                        "should exist"
                );
            }

            count = 0;
            try {
                count = coverLetterRepository.deleteByIdAndMemberId(
                    jobEntity.getCoverLetterId(), userId
                );
            } catch(RuntimeException err) {
                throw new SQLDeleteFailedException(
                    "Delete cover letter failed during delete", err
                );
            }
            if (count < 1) {
                throw new SQLDeleteFailedException(
                    "Delete didn't delete cover letter during delete, even " +
                        "though it should"
                );
            }
        }

        // TODO: 4.) and 5.) Delete files from S3


        // ------------ 6.) Return deleted job with files -------------
        return JobMapper.toResponseDtoWithFiles(
            jobEntity,
            resume,
            coverLetter
        );
    }
}

// Convert string to UUID
//   UUID.fromString("1d27e3ee-1111-4e0d-ac0f-dadfcc420ce3"),

// ---------- Spring Data JPA Query Methods -------
// https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html

// TODO: (5/19/25)
//  - The original Node version returns undefined when something
//    goes wrong (other than cases that should return undefined such as a
//    single job being "get" not existing). I didn't use transactions :(, but
//    for this Java/Spring version I did :).
//    -- I should throw an exception where appropriate, then just return
//       null for those since that's what the frontend expects
//  - Maybe I should update the logic so that modifying queries (at least for
//    delete) doesn't return the job, resume, and cover letter.
//    - IMPORTANT: I'm doing this since I need to lessen the number of requests
//      to the database, since JPA doesn't return the entity for modifying
//      queries
//  - Should I put a try catch for @Transactional or for Spring Data JPA
//    queries?
//    -- Or does Spring, Spring Boot, Spring MVC, and/or etc. do this for
//       me automatically?
//    -- IMPORTANT: I may want to do this still, then just return a general
//       exception for everything. Since for now, I only care about being able
//       to catch so I can return null when something fails.

// TODO: How does Spring, Spring MVC, Hibernate, and/or Spring Data JPA
//   handle exceptions?
//   - Since I want to return a JSON error object based on the error
//     -- DO THAT LATER.
//     -- For now, just return null and set the status code
//   - Look at @ControllerAdvice

// Exceptions:
// - https://stackoverflow.com/questions/2683182/how-and-where-do-you-define-your-own-exception-hierarchy-in-java
// - https://www.reddit.com/r/java/comments/198q4le/how_do_you_structure_your_exception_classes/
// - https://www.reddit.com/r/learnjava/comments/1fbm2lm/does_every_custom_exception_need_its_own_class/
// - https://java-programming.mooc.fi/part-11/3-exceptions
//   -- GREAT READ

// Checked vs Unchecked Exceptions:
// - Java exceptions fall into two main categories: checked exceptions and unchecked exceptions.
// - https://stackoverflow.com/questions/2190161/difference-between-java-lang-runtimeexception-and-java-lang-exception
//   -- Good read
// - https://www.baeldung.com/java-checked-unchecked-exceptions
//   -- GREAT READ
//   -- In general, checked exceptions represent errors outside the control of the program.
//      For example, the constructor of FileInputStream throws FileNotFoundException if the input file does not exist.
//      Java verifies checked exceptions at compile-time.
//      Therefore, we should use the throws keyword to declare a checked exception
//      We can also use a try-catch block to handle a checked exception
//      Ex. IOException, SQLException and ParseException
//      The Exception class is the superclass of checked exceptions, so we can create a custom checked exception by extending Exception
//   -- If a program throws an unchecked exception, it reflects some error inside the program logic.
//      For example, if we divide a number by 0, Java will throw ArithmeticException
//      ME: NullPointerException, ArrayIndexOutOfBoundsException, IllegalArgumentException, etc.
//      + So basically, some logic error within the code that we caused as the
//        programmer by not handling it (Ex. not doing null checks, out of
//        bounds checks, etc.)
//      Java does not verify unchecked exceptions at compile-time. Furthermore,
//        we don’t have to declare unchecked exceptions in a method with the
//        throws keyword. And although the above code does not have any errors
//        during compile-time, it will throw ArithmeticException at runtime
//      The RuntimeException class is the superclass of all unchecked
//        exceptions, so we can create a custom unchecked exception by
//        extending RuntimeException
//   -- The Oracle Java Documentation provides guidance on when to use checked
//      exceptions and unchecked exceptions:
//        “If a client can reasonably be expected to recover from an exception,
//        make it a checked exception. If a client cannot do anything to
//        recover from the exception, make it an unchecked exception.”
//      + For example, before we open a file, we can first validate the input
//        file name. If the user input file name is invalid, we can throw a
//        custom checked exception (Instead of FileNotFound if the file with
//        the given name doesn't exist, which is a checked exception)
//      + However, if the input file name is a null pointer or it is an empty
//        string, it means that we have some errors in the code. In this
//        case, we should throw an unchecked exception:
//   -- https://docs.oracle.com/javase/tutorial/essential/exceptions/runtime.html


// Exceptions in Spring, Spring MVC, Spring Boot, etc.
// - https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
//   -- Your users do not want to see web-pages containing Java exception
//      details and stack-traces. You may have security policies that
//      expressly forbid putting any exception information in the error page.
//      Another reason to make sure you override the Spring Boot white-label
//      error page
//   -- If you aren't using server-side rendering 2.1 Define your own error
//      View as a bean called error. 2.1 Or disable Spring boot's
//      "Whitelabel" error page by setting the property:
//        server.error.whitelabel.enabled to false.
//          Your container's default error page is used instead
//        Found in application.properties
// - https://www.reddit.com/r/SpringBoot/comments/172urpj/what_is_the_standardized_way_to_handle_response/
// - https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-exceptionhandler.html
// - https://www.baeldung.com/exception-handling-for-rest-with-spring
// - https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-advice.html
// - https://www.baeldung.com/exception-handling-for-rest-with-spring


// Exceptions w/ @Transactional
// - https://www.marcobehler.com/guides/spring-transaction-management-transactional-in-depth
//   -- IMPORTANT: You also do not have to catch SQLExceptions, as Spring converts these
//      exceptions to runtime exceptions for you
//   -- Comment by Marco Behler:
//      Here, taken straight from the Spring documentation:
//      By default, a transaction will be rolling back on RuntimeException and Error
//        but not on checked exceptions (business exceptions).
//      Now, you might be hinting at "checked" exceptions, but then again "using" the
//        connection throws a SQLException inevitably, which Spring will internally
//        wrap in its own (runtime) exception, which will result in a rollback as well
//      TODO: (5/20/25) Read up on how checked exceptions don't cause rollbacks
//       Some useful notes:
//       You also do not have to catch SQLExceptions, as Spring converts these
//         exceptions to runtime exceptions for you
// - https://stackoverflow.com/questions/7125837/why-does-transaction-roll-back-on-runtimeexception-but-not-sqlexception
//   -- Any RuntimeException triggers rollback, and any checked Exception does not
//   -- The rationale behind this is that RuntimeException classes are
//      generally taken by Spring to denote unrecoverable error conditions
//   -- This behaviour can be changed from the default, if you wish to do so,
//      but how to do this depends on how you use the Spring API, and how you
//      set up your transaction manager
//      + TODO: (5/20/25) I can probably just catch checked exceptions, then
//         throw a runtime exception
//         BUT, looking at https://www.reddit.com/r/SpringBoot/comments/16gubkp/if_transactional_throws_an_exception_can_this_try/
//           Don't use try catch. Just do @Transactional(rollbackFor...
//         We can also set @Transactional(rollbackFor=Exception.class)
//         BUT, check if this makes it so that it only rolls back for
//         Exception and its subclasses
// - https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html
//   -- Any RuntimeException or Error triggers rollback, and any checkedException does not
//   -- As of 6.2, you can globally change the default rollback behavior – for example,
//      through @EnableTransactionManagement(rollbackOn=ALL_EXCEPTIONS), leading to a rollback
//      for all exceptions raised within a transaction, including any checked exception
// - https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/rolling-back.html#transaction-declarative-rollback-rules
// - https://www.reddit.com/r/SpringBoot/comments/16gubkp/if_transactional_throws_an_exception_can_this_try/
//   -- GREAT INFO. Don't use try catch. Just do @Transactional(rollbackFor...
//      + ME: I can actually try catch, but need to throw an exception instead of
//        handling
//   -- If you catch it @Transactional will not work. Because @Transactional
//      generates a proxy which catches exceptions from the annotated method
//      and the performs a rollback. If no exception is thrown by the annotated
//      method no rollback will happen
//   -- so if i want to catch it with a custom exception something like this i
//      have to use rollbackFor attribute or handle the exception in a higher level right?
//      + Yes
// - https://medium.com/@Mohd_Aamir_17/the-transactional-dilemma-how-it-affects-exception-handling-in-your-spring-boot-applications-ea613c8d55b9
//   -- How @Transactional affects Exception Handling
//   -- Scenario 1: If an exception occurs and the transaction is rolled back,
//      Spring will silently suppress the original exception and instead throw an UnexpectedRollbackException
//   -- When a method annotated with @Transactional throws a checked or
//      unchecked exception, Spring’s transaction manager marks the transaction
//      for rollback. However, in certain cases, the rollback happens silently,
//      and the transaction manager throws an UnexpectedRollbackException,
//      obscuring the original cause of the error
//   -- TODO: (5/20/15) For my RestControllerAdvice, I should include this as
//       one of the classes one of the handlers deal with
//   -- @Transactional(rollbackFor = {Exception.class})
//      + To rollback checked exceptions
//   -- Ensure your unit and integration tests cover scenarios where
//      transactions are rolled back.This will help you catch unexpected
//      behaviors early in development
// - https://stackoverflow.com/questions/52456783/cannot-catch-dataintegrityviolationexception
//   -- Need to catch DataIntegrityViolationException outside the
//      @Transactional (ex. In the controller) since the exception doesn't
//      occur until the transaction commits
// - http://stackoverflow.com/questions/76063462/in-springboot-does-transactional-annotation-still-rollbacks-the-transaction-if

// Exceptions w/ JPA, Spring Data JPA, Hibernate
// - https://stackoverflow.com/questions/23991596/do-i-have-to-try-catch-jparepository
//   -- Repositories will always tell you something if a problem happens (i.e. they never swallow exceptions).
//     You'll always get a runtime exception if that's the case
// - https://www.reddit.com/r/javahelp/comments/1dsd1dx/do_you_guys_have_it_memorized_what_exceptions/
// - https://www.reddit.com/r/learnjava/comments/12fbz71/jpa_trycatch/
// - https://stackoverflow.com/questions/71300043/spring-data-jpa-how-to-implement-proper-exception-handling

// Custom Exceptions
// - https://www.baeldung.com/java-new-custom-exception
// - https://stackify.com/java-custom-exceptions/
//   -- https://stackify.com/best-practices-exceptions-java/
// - *** The 3 above are really good
// - https://www.reddit.com/r/java/comments/198q4le/how_do_you_structure_your_exception_classes/
// - https://stackoverflow.com/questions/67284925/how-to-handle-a-custom-exception-with-errorhandler-for-httpclienterrorexception
// - https://www.baeldung.com/java-common-exceptions
//   -- List of common exceptions

// TODO: (6/2/25)
//  Use these 4 resources mainly:
//  - https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
//  - https://www.baeldung.com/exception-handling-for-rest-with-spring
//  - https://www.baeldung.com/java-new-custom-exception
//  - https://stackify.com/java-custom-exceptions/
//  What custom exceptions do I need?
//  - First, what errors could happen?
//    -- In SQL: not found, edit failed, delete failed
//    -- In S3: get, put, delete failed
//    -- In code: some conversion failed, etc.
//  In RestControllerAdvice:
//  - I want a handler for the custom exceptions
//  - I also want a general handler for all Exceptions and RuntimeExceptions
//    -- Ex. A service might throw an exception that I didn't catch then
//           rethrew as a custom exception (such as an exception in one of my
//           custom mappers)
//  (6/3/25)
//  - Review the code
//  - Edit the controllers. Look at parts where it was initially expecting
//    null, since the RestControllerAdvice now handles most of these cases
//    except in getJob when the job isn't found
