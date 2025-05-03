package com.qu1cksave.qu1cksave_backend.resume;

import com.fasterxml.jackson.annotation.JsonProperty;


public class RequestResumeDto {
    private final String fileName; // NOT NULLABLE
    private final String mimeType; // NOT NULLABLE

    // From qu1cksave backend Express-version:
    // - bytearray_as_array: number[];  // Changed from Uint8Array to number[]
    // https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number
    // - The JavaScript Number type is a double-precision 64-bit binary format IEEE 754 value, like double in Java or C#.
    // TODO: (3/29/25) Maybe I can use long?
    //  Though, there's also byte[]
    //  I'm using double temporarily for now
    //  long can also work
    private final double[] byteArrayAsArray;

    // Constructors
    // Need JsonProperty in constructor params so Jackson knows how to
    //   deserialize
    public RequestResumeDto(
        // TODO: (5/2/25) Might want to change this to snake_case
        @JsonProperty("fileName") String fileName,
        @JsonProperty("mimeType") String mimeType,
        @JsonProperty("byteArrayAsArray") double[] byteArrayAsArray
    ) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.byteArrayAsArray = byteArrayAsArray;
    }

    // Getters
    public String getFileName() { return fileName; }
    public String getMimeType() { return mimeType; }
    public double[] getByteArrayAsArray() { return byteArrayAsArray; }
}
