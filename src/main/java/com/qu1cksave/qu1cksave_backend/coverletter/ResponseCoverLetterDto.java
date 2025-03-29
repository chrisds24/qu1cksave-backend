package com.qu1cksave.qu1cksave_backend.coverletter;

import com.fasterxml.jackson.annotation.JsonProperty;

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
        @JsonProperty("memberId") UUID memberId,
        @JsonProperty("fileName") String fileName,
        @JsonProperty("mimeType") String mimeType
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


//    @Override
//    public String toString() {
//        return String.format("{\n\tid: %s,\n\tmemberId: %s,\n\tfileName: %s,\n\tmimeType: %s\n}", id, memberId, fileName, mimeType);
//    }
}
