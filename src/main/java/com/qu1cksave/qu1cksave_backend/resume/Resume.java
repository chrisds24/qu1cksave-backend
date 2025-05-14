package com.qu1cksave.qu1cksave_backend.resume;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.util.UUID;

@Entity
@Table(name = "resume")
public class Resume {
    // TODO (Note 1): There's something about how a PostgreSQL query fills
    //  empty columns as null due to LEFT JOIN, so each field should be its
    //  actual type or null (Ex. id: string | null    in TypeScript)
    //  - This is a note from my old Node/Express version of the backend
    //  - This happens when getting a job (in my case, getAllJobs) and the job
    //    doesn't have an associated resume
    //  HOWEVER, the columns in the table itself have NOT NULL (can't be null)
    //  I may not need to worry about this depending on how Hibernate
    //    fetches Jobs joined with Resume, such as if it just leaves the Resume
    //    column for that job as null instead of including a Resume with null
    //    columns

    // NOTE: The Resume/CoverLetter controllers are simply for getting the
    //   Resume/CoverLetter when downloading them.

    @Generated
    @Id
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    @Column(name = "member_id", nullable = false)
    private UUID memberId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    // TODO: Need another Resume-type class that has the equivalent of
    //   bytearray_as_array?: number[] (TypeScript) in Java, which is part of
    //   the Resume that will be returned when its downloaded.
    //   - Or maybe there's a way to do this through annotations?

    // Constructors

    protected Resume() {}

    public Resume(
        UUID id,
        UUID memberId,
        String fileName,
        String mimeType
    ) {
        this.id = id;
        this.memberId = memberId;
        this.fileName = fileName;
        this.mimeType = mimeType;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getMemberId() { return memberId; }
    public String getFileName() { return fileName; }
    public String getMimeType() { return mimeType; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setMemberId(UUID memberId) { this.memberId = memberId; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
}
