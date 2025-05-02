package com.qu1cksave.qu1cksave_backend.job;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

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
}
