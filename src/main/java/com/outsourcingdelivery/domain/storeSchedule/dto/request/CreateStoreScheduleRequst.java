package com.outsourcingdelivery.domain.storeSchedule.dto.request;

import lombok.Getter;

@Getter
public class CreateStoreScheduleRequst {
    private String dayOfWeek;
    private String openTime;
    private String closeTime;
}
