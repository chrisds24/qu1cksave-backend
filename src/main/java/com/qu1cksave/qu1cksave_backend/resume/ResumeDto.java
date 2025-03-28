package com.qu1cksave.qu1cksave_backend.resume;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class ResumeDto {

    private final UUID id;

    private final UUID memberId;

    private final String fileName;

    private final String mimeType;

    // TODO: Need an Integer[] byteArrayAsArray field
    // - Have a separate ResumeWithFileDto

    // Constructors
    // Need JsonProperty in constructor params so Jackson knows how to
    //   deserialize
    public ResumeDto(
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
