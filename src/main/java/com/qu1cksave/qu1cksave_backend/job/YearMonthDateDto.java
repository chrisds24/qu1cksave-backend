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

    public YearMonthDateDto(
        @JsonProperty("year") Integer year,
        @JsonProperty("month") Integer month,
        @JsonProperty("date") Integer date
    ) {
        this.year = year;
        this.month = month;
        this.date = date;
    }

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
