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