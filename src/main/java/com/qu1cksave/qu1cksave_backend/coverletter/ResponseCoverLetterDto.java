package com.qu1cksave.qu1cksave_backend.coverletter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qu1cksave.qu1cksave_backend.resume.ResponseResumeDto;

import java.util.Objects;
import java.util.UUID;

public class ResponseCoverLetterDto {
    private final UUID id;

    private final UUID memberId;

    private final String fileName;

    private final String mimeType;

    // TODO: Need an Integer[] byteArrayAsArray field
    // - Have a separate CoverLetterWithFileDto

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

        // Not a ResponseResumeDto, can't be same object
        if (!(comparedObject instanceof ResponseResumeDto)) {
            return false;
        }

        ResponseResumeDto comparedResponseResumeDto =
            (ResponseResumeDto) comparedObject;

        // Compare instance variables
        return Objects.equals(this.getId(), comparedResponseResumeDto.getId()) &&
            Objects.equals(this.getMemberId(), comparedResponseResumeDto.getMemberId()) &&
            Objects.equals(this.getFileName(), comparedResponseResumeDto.getFileName()) &&
            Objects.equals(this.getMimeType(), comparedResponseResumeDto.getMimeType());
    }
}
