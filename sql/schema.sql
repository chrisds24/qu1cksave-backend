DROP TABLE IF EXISTS job;
DROP TABLE IF EXISTS member;
DROP TABLE IF EXISTS resume;

-- Columns are: id, email, password, roles, name
CREATE TABLE member(id UUID UNIQUE PRIMARY KEY DEFAULT gen_random_uuid(), email VARCHAR(254) UNIQUE, password TEXT, roles jsonb, name VARCHAR(255));
-- Add cover letter later
-- Columns are: id, member_id, resume_id, cover_letter_id, title, company_name, job_description, notes, is_remote, salary_min, salary_max, country, us_state, city, date_saved, date_applied, date_posted, job_status, links, found_from
-- description and notes used to be VARCHAR(12000). Then it was changed to TEXT. Then they were changed to VARCHAR(16384)
-- is_remote is VARCHAR(10) just because it seems like a good max limit to remote options. Same idea for job_status
-- us_state is VARCHAR(3) because the longest us_state is N/A
CREATE TABLE job(id UUID UNIQUE PRIMARY KEY DEFAULT gen_random_uuid(), member_id UUID NOT NULL, resume_id UUID, cover_letter_id UUID, title VARCHAR(255) NOT NULL, company_name VARCHAR(255) NOT NULL, job_description VARCHAR(16384), notes VARCHAR(16384), is_remote VARCHAR(10) NOT NULL, salary_min integer, salary_max integer, country VARCHAR(255), us_state VARCHAR(3), city VARCHAR(255), date_saved TIMESTAMPTZ NOT NULL DEFAULT NOW(), date_applied jsonb, date_posted jsonb, job_status VARCHAR(20) NOT NULL, links jsonb, found_from VARCHAR(255));
-- Columns are: id, member_id, file_name, mime_type
CREATE TABLE resume(id UUID UNIQUE PRIMARY KEY DEFAULT gen_random_uuid(), member_id UUID NOT NULL, file_name VARCHAR(255) NOT NULL, mime_type VARCHAR(255) NOT NULL);
CREATE TABLE cover_letter(id UUID UNIQUE PRIMARY KEY DEFAULT gen_random_uuid(), member_id UUID NOT NULL, file_name VARCHAR(255) NOT NULL, mime_type VARCHAR(255) NOT NULL);

-- FOREIGN KEYS
-- https://www.postgresql.org/docs/current/tutorial-fk.html
-- https://www.postgresql.org/docs/current/ddl-constraints.html#DDL-CONSTRAINTS-FK
-- https://stackoverflow.com/questions/46184534/reference-to-foreign-key-row-postgresql

-- ------------------ Relationships --------------------
-- Job belongs to a member.
--     Job has reference to Member using member_id, but Member doesn't.
--     But, a Member can get all their jobs by filtering using each job's member_id
-- Resume (also for CoverLetter)
--     Resume belongs to a Job (and Member).
--         The idea is that this is the Resume used for this Job application.
--         It obviously needs a member_id to signify which Member posted the Resume
--  Job, Member (Many-to-one)       Many Jobs for one Member (A Job belongs to only one Member)
--  Job, Resume (One-to-one)        One Resume for one Job (A Resume belongs to only one Job)
--  Resume, Member (Many-to-one)    Many Resumes for one Member (A Resume belongs to only one Member)
-- ...........
-- From what we have above, we can:
-- 1.) A. Get the Resume that belongs to a Job using the Job's resume_id
--        - Owner has foreign key
--        - We can do a LEFT JOIN to get the Resume that belongs to that Job.
--     B. Find which Job a Resume belongs to (select Job then filter by resume_id, since a Job only has one resume_id)
-- 2.) A. Members can get Jobs that belong to them (select Jobs then filter by member_id)
--     B. Find which Member a Job belongs to using the Job's member_id
--        - The one being owned has foreign key
--        - Can do a LEFT JOIN to get the Member who owns this Job
-- 3.) A. Members can get Resumes that belong to them (select Resume then filter by member_id)
--     B. Find which Member a Resume belongs to using the Resume's member_id
--        - The one being owned has the foreign key
--        - Can do a LEFT JOIN to get the Member who owns this Resume
-- Notice:
-- 1.) A. Find which Job a Resume belongs to (select Job then filter by resume_id, since a Job only has one resume_id)
--        - The owner (Job) has the foreign key
--        - Can't do a LEFT JOIN to get the Job who owns this Resume
--          + At the very least, it's not as simple as the usual way
-- 3.) B. Find which Member a Resume belongs to using the Resume's member_id
--        - The one being owned (Resume) has the foreign key
--        - Can do a LEFT JOIN to get the Member who owns this Resume
-- *** There's an inconsistency with how I put which has which id and doesn't
--     A join table would have simplified everything and made things more flexible
--     For example, my schema is not flexible since a Job can only have one Resume, but I might later want a Job to have
--       multiple ones. I might also want a Resume to be associated with many jobs

