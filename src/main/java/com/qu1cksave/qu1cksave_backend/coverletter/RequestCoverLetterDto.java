package com.qu1cksave.qu1cksave_backend.coverletter;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestCoverLetterDto {
    private final String fileName;

    private final String mimeType;

    // From qu1cksave backend Express-version:
    // - bytearray_as_array: number[];  // Changed from Uint8Array to number[]
    // https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number
    // - The JavaScript Number type is a double-precision 64-bit binary format IEEE 754 value, like double in Java or C#.
    // TODO: (3/29/25) Maybe I can use long?
    //  Though, there's also byte[]
    //  I'm using long temporarily for now
    private final long[] byteArrayAsArray;

    // Constructors
    // Need JsonProperty in constructor params so Jackson knows how to
    //   deserialize
    public RequestCoverLetterDto(
        @JsonProperty("fileName") String fileName,
        @JsonProperty("mimeType") String mimeType,
        @JsonProperty("byteArrayAsArray") long[] byteArrayAsArray
    ) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.byteArrayAsArray = byteArrayAsArray;
    }

    // Getters
    public String getFileName() { return fileName; }
    public String getMimeType() { return mimeType; }
    public long[] getByteArrayAsArray() { return byteArrayAsArray; }
}
