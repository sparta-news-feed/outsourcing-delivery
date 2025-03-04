package com.outsourcingdelivery.domain.storeSchedule.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateStoreScheduleRequst {
    @NotNull(message = "요일 입력은 필수입니다.")
    private String dayOfWeek;
    @NotNull(message = "오픈시간 입력은 필수입니다.")
    private String openTime;
    @NotNull(message = "마감시간 입력은 필수입니다.")
    private String closeTime;
}
