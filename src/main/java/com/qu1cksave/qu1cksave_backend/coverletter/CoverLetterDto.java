package com.qu1cksave.qu1cksave_backend.coverletter;

import java.util.UUID;

public class CoverLetterDto {
    private final UUID id;

    private final UUID memberId;

    private final String fileName;

    private final String mimeType;

    // TODO: Need an Integer[] byteArrayAsArray field

    // Constructors

    public CoverLetterDto(
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
}
