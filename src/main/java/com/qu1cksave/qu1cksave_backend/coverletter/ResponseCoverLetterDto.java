package com.qu1cksave.qu1cksave_backend.coverletter;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

public class ResponseCoverLetterDto {
    private final UUID id;

    private final UUID memberId;

    private final String fileName;

    private final String mimeType;

    // Constructors
    public ResponseCoverLetterDto(
        @JsonProperty("id") UUID id,
        @JsonProperty("member_id") UUID memberId,
        @JsonProperty("file_name") String fileName,
        @JsonProperty("mime_type") String mimeType
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

    @Override
    public boolean equals(Object comparedObject) {
        // Same memory location, so same object
        if (this == comparedObject) {
            return true;
        }

        // Not a ResponseCoverLetterDto, can't be same object
        if (!(comparedObject instanceof ResponseCoverLetterDto)) {
            return false;
        }

        ResponseCoverLetterDto comparedResponseCoverLetterDto =
            (ResponseCoverLetterDto) comparedObject;

        // Compare instance variables
        return Objects.equals(this.getId(), comparedResponseCoverLetterDto.getId()) &&
            Objects.equals(this.getMemberId(), comparedResponseCoverLetterDto.getMemberId()) &&
            Objects.equals(this.getFileName(), comparedResponseCoverLetterDto.getFileName()) &&
            Objects.equals(this.getMimeType(), comparedResponseCoverLetterDto.getMimeType());
    }
}
