package com.qu1cksave.qu1cksave_backend.coverletter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class ResponseCoverLetterDto {
    private final UUID id;

    private final UUID memberId;

    private final String fileName;

    private final String mimeType;

    private double[] byteArrayAsArray;

    // Constructors
    public ResponseCoverLetterDto(
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
            Objects.equals(this.getMimeType(), comparedResponseCoverLetterDto.getMimeType()) &&
            Arrays.equals(this.getByteArrayAsArray(), comparedResponseCoverLetterDto.getByteArrayAsArray());
    }
}
