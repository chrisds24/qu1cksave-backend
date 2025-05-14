package com.qu1cksave.qu1cksave_backend.job;

import jakarta.persistence.Embeddable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Embeddable
public class YearMonthDate {

    private Integer year;
    private Integer month;
    private Integer date;

    protected YearMonthDate() {}

    public YearMonthDate(
        Integer year,
        Integer month,
        Integer date
    ) {
        this.year = year;
        this.month = month;
        this.date = date;
    }

    // Getters
    public Integer getYear() { return year; }
    public Integer getMonth() { return month; }
    public Integer getDate() { return date; }

    // Setters
    public void setYear(Integer year) { this.year = year; }
    public void setMonth(Integer month) { this.month = month; }
    public void setDate(Integer date) { this.date = date; }

    @Override
    public boolean equals(Object comparedObject) {
        // If same memory location
        if (this == comparedObject) {
            return true;
        }

        // If not a YearMonthDateDto, can't be the same object
        if (!(comparedObject instanceof YearMonthDate)) {
            return false;
        }

        YearMonthDate comparedYearMonthDate = (YearMonthDate) comparedObject;

        // If same instance variables, same object
        return Objects.equals(this.getYear(), comparedYearMonthDate.getYear()) &&
            Objects.equals(this.getMonth(), comparedYearMonthDate.getMonth()) &&
            Objects.equals(this.getDate(), comparedYearMonthDate.getDate());
    }

    // Map<String, Object> to YearMonthDate
//    public static YearMonthDate toYearMonthDate(Map<String, Object> mapYearMonthDate) {
//        return new YearMonthDate(
//            Integer.valueOf(String.valueOf(mapYearMonthDate.get("year"))),
//            Integer.valueOf(String.valueOf(mapYearMonthDate.get("month"))),
//            Integer.valueOf(String.valueOf(mapYearMonthDate.get("date")))
//        );
//    }

//    public static Map<String, Object> toMap(YearMonthDate yearMonthDate) {
//        Map<String, Object> mapYearMonthDate = new HashMap<String, Object>();
//        mapYearMonthDate.put("year", yearMonthDate.getYear().toString());
//        mapYearMonthDate.put("month", yearMonthDate.getMonth().toString());
//        mapYearMonthDate.put("date", yearMonthDate.getDate().toString());
//        return mapYearMonthDate;
//    }
}
