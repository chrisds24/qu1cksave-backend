package com.qu1cksave.qu1cksave_backend.coverletter;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

public class CoverLetter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private UUID id;

    @Column(name = "member_id", nullable = false)
    private UUID memberId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    // Constructors

    protected CoverLetter() {}

    public CoverLetter(
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
