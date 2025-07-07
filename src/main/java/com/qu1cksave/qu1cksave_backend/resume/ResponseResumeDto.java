package com.qu1cksave.qu1cksave_backend.resume;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class ResponseResumeDto {

    private final UUID id;

    private final UUID memberId;

    private final String fileName;

    private final String mimeType;

    // TODO: (3/29/25) Need a byteArrayAsArray field
    //  https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number
    //  - A number literal like 37 in JavaScript code is a floating-point value, not an integer
    //  - The JavaScript Number type is a double-precision 64-bit binary format IEEE 754 value, like double in Java or C#
    //  Therefore, I'm using double in Java
    private double[] byteArrayAsArray;

    // Constructors
    // Need JsonProperty in constructor params so Jackson knows how to
    //   deserialize
    public ResponseResumeDto(
        @JsonProperty("id") UUID id,
        @JsonProperty("member_id") UUID memberId,
        @JsonProperty("file_name") String fileName,
        @JsonProperty("mime_type") String mimeType,
        @JsonProperty("byte_array_as_array") Object byteArrayAsArray
    ) {
        this.id = id;
        this.memberId = memberId;
        this.fileName = fileName;
        this.mimeType = mimeType;

        if (byteArrayAsArray != null) {
            ObjectMapper objectMapper = new ObjectMapper();

            if (byteArrayAsArray instanceof double[]) {
                this.byteArrayAsArray = (double[]) byteArrayAsArray;
            } else if (byteArrayAsArray instanceof String) {
                try {
                    this.byteArrayAsArray = objectMapper.readValue((String) byteArrayAsArray, double[].class);
                } catch (JsonProcessingException err) {
                    throw new RuntimeException(err);
                }
            } else { // ArrayList
                ArrayList arrLst = (ArrayList) byteArrayAsArray;
                int n = arrLst.size();
                double[] arr = new double[n];
                for (int i = 0; i < n; i++) {
                    arr[i] = (Double) arrLst.get(i);
                }
                this.byteArrayAsArray = arr;
            }
        }
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getMemberId() { return memberId; }
    public String getFileName() { return fileName; }
    public String getMimeType() { return mimeType; }
    public double[] getByteArrayAsArray() { return byteArrayAsArray; }

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
            Objects.equals(this.getMimeType(), comparedResponseResumeDto.getMimeType()) &&
            Arrays.equals(this.getByteArrayAsArray(), comparedResponseResumeDto.getByteArrayAsArray());
    }
}
