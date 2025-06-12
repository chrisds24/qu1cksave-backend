package com.qu1cksave.qu1cksave_backend.job;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

public class YearMonthDateDto {
    private final Integer year;
    private final Integer month;
    private final Integer date;

//    @JsonCreator // TODO: Maybe not needed?
    public YearMonthDateDto(
        @JsonProperty("year") Integer year,
        @JsonProperty("month") Integer month,
        @JsonProperty("date") Integer date
    ) {
        this.year = year;
        this.month = month;
        this.date = date;
    }

    // I created this constructor to solve:
//    org.springframework.core.codec.DecodingException: JSON decoding error: Cannot construct instance of `com.qu1cksave.qu1cksave_backend.job.YearMonthDateDto` (although at least one Creator exists): no String-argument constructor/factory method to deserialize from String value ('date_applied')
    // However, I now have to deal with:
////    org.springframework.core.codec.DecodingException: JSON decoding error: Cannot construct instance of `com.qu1cksave.qu1cksave_backend.job.YearMonthDateDto`, problem: Unrecognized token 'date_applied': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')
//    public YearMonthDateDto(
//        String yearMonthDateDto
//    ) throws JsonProcessingException {
//        // Weird since I'm creating an instance from readValue, then
//        //   the fields from that instance to the instance to be created
//        //   through this constructor
//        ObjectMapper objectMapper = new ObjectMapper();
//        YearMonthDateDto ymdDto = yearMonthDateDto != null ? objectMapper.readValue(yearMonthDateDto, YearMonthDateDto.class) : null;
//        if (ymdDto != null) {
//            this.year = ymdDto.getYear();
//            this.month = ymdDto.getMonth();
//            this.date = ymdDto.getDate();
//        }
//    }

    // Getters
    public Integer getYear() { return year; }
    public Integer getMonth() { return month; }
    public Integer getDate() { return date; }

    @Override
    public boolean equals(Object comparedObject) {
        // If same memory location
        if (this == comparedObject) {
            return true;
        }

        // If not a YearMonthDateDto, can't be the same object
        if (!(comparedObject instanceof YearMonthDateDto)) {
            return false;
        }

        YearMonthDateDto comparedYearMonthDateDto = (YearMonthDateDto) comparedObject;

        // If same instance variables, same object
        return Objects.equals(this.getYear(), comparedYearMonthDateDto.getYear()) &&
            Objects.equals(this.getMonth(), comparedYearMonthDateDto.getMonth()) &&
            Objects.equals(this.getDate(), comparedYearMonthDateDto.getDate());
    }
}
