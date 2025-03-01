package com.qu1cksave.qu1cksave_backend.job;

import jakarta.persistence.Embeddable;

@Embeddable
public class YearMonthDate {

    private int year;
    private int month;
    private int date;

    public YearMonthDate() {}

    public YearMonthDate(
        int year,
        int month,
        int date
    ) {
        this.year = year;
        this.month = month;
        this.date = date;
    }

    // Getters
    public int getYear() { return year; }
    public int getMonth() { return month; }
    public int getDate() { return date; }

    // Setters
    public void setYear(int year) { this.year = year; }
    public void setMonth(int month) { this.month = month; }
    public void setDate(int date) { this.date = date; }
}
