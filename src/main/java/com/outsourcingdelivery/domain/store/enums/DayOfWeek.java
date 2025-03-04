package com.outsourcingdelivery.domain.store.enums;

import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;

import java.util.Arrays;

public enum DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    public static DayOfWeek of(String type) {
        return Arrays.stream(DayOfWeek.values())
                .filter(t -> t.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_DAY_DF_WEEK_ENUM_VALUE));
    }
}
