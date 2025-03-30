package com.qu1cksave.qu1cksave_backend.job;

public class YearMonthDateMapper {
    public static YearMonthDateDto toDto(YearMonthDate embeddable) {
        return new YearMonthDateDto(
            embeddable.getYear(),
            embeddable.getMonth(),
            embeddable.getDate()
        );
    }

    public static YearMonthDate toEmbeddable(YearMonthDateDto dto) {
        return new YearMonthDate(
            dto.getYear(),
            dto.getMonth(),
            dto.getDate()
        );
    }
}
