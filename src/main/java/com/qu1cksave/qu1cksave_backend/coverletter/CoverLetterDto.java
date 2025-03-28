package com.qu1cksave.qu1cksave_backend.coverletter;

import java.util.UUID;

public class CoverLetterDto {
    private final UUID id;

    private final UUID memberId;

    private final String fileName;

    private final String mimeType;

    // TODO: Need an Integer[] byteArrayAsArray field
    // - Have a separate CoverLetterWithFileDto

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


//    @Override
//    public String toString() {
//        return String.format("{\n\tid: %s,\n\tmemberId: %s,\n\tfileName: %s,\n\tmimeType: %s\n}", id, memberId, fileName, mimeType);
//    }
}
