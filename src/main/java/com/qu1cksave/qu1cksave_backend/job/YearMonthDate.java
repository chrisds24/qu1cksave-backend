package com.qu1cksave.qu1cksave_backend.job;

import jakarta.persistence.Embeddable;

import java.util.HashMap;
import java.util.Map;

// UPDATE: (3/28/25) I ended up not using this due to some conversion issue
//   from the Repository result to this in the Job entity
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

    // Map<String, Object> to YearMonthDate
    public static YearMonthDate toYearMonthDate(Map<String, Object> mapYearMonthDate) {
        return new YearMonthDate(
            Integer.valueOf(String.valueOf(mapYearMonthDate.get("year"))),
            Integer.valueOf(String.valueOf(mapYearMonthDate.get("month"))),
            Integer.valueOf(String.valueOf(mapYearMonthDate.get("date")))
        );
    }

    public static Map<String, Object> toMap(YearMonthDate yearMonthDate) {
        Map<String, Object> mapYearMonthDate = new HashMap<String, Object>();
        mapYearMonthDate.put("year", yearMonthDate.getYear().toString());
        mapYearMonthDate.put("month", yearMonthDate.getMonth().toString());
        mapYearMonthDate.put("date", yearMonthDate.getDate().toString());
        return mapYearMonthDate;
    }
}
