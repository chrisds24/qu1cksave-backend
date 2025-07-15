package com.qu1cksave.qu1cksave_backend.resume;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;


public class RequestResumeDto {
    @NotNull
    private final String fileName;
    @NotNull
    private final String mimeType;
    
    @NotNull
    private final double[] byteArrayAsArray;

    // Constructors
    // Need JsonProperty in constructor params so Jackson knows how to
    //   deserialize
    public RequestResumeDto(
        @JsonProperty("file_name") String fileName,
        @JsonProperty("mime_type") String mimeType,
        @JsonProperty("byte_array_as_array") double[] byteArrayAsArray
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
