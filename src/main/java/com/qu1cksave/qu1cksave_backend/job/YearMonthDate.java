package com.qu1cksave.qu1cksave_backend.job;

import jakarta.persistence.Embeddable;

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
}
