package com.outsourcingdelivery.domain.storeSchedule.dto.request;

import com.outsourcingdelivery.domain.storeSchedule.enums.DayOfWeek;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class StoreScheduleRequest {
    @NotNull(message = "요일 입력은 필수입니다.")
    private DayOfWeek dayOfWeek;
    @NotNull(message = "오픈시간 입력은 필수입니다.")
    private LocalTime openTime;
    @NotNull(message = "마감시간 입력은 필수입니다.")
    private LocalTime closeTime;
}
