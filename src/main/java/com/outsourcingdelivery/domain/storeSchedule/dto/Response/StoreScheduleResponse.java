package com.outsourcingdelivery.domain.storeSchedule.dto.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.outsourcingdelivery.domain.storeSchedule.enums.DayOfWeek;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class StoreScheduleResponse {
    private final DayOfWeek dayOfWeek;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private final LocalTime openTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private final LocalTime closeTime;

    public StoreScheduleResponse(DayOfWeek dayOfWeek, LocalTime openTime, LocalTime closeTime) {
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }
}
