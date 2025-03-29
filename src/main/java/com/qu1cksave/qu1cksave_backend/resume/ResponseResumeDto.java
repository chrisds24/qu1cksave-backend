package com.qu1cksave.qu1cksave_backend.resume;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class ResponseResumeDto {

    private final UUID id;

    private final UUID memberId;

    private final String fileName;

    private final String mimeType;

    // TODO: (3/29/25) Need a byteArrayAsArray field
    //  FIRST: Check if getting the list of jobs still works even with this
    //  Otherwise, I'll need a separate ResumeWithFileDto needed?
    //  I was originally thinking of using Integer[] (or int[]), but the size
    //    of a number in JavaScript is 64 bits
    //  - So long[] might be more appropriate (Also, double is also 64 bits)
    //  - But there's also byte[] as an option
    //  UPDATE: Since this is a DTO, I can't set its byteArrayAsArray later
    //    byteArrayAsArray also doesn't come from the database
    //    So I'll definitely need a ResponseResumeWithFilesDto
//    private final long[] byteArrayAsArray;

    // Constructors
    // Need JsonProperty in constructor params so Jackson knows how to
    //   deserialize
    public ResponseResumeDto(
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
