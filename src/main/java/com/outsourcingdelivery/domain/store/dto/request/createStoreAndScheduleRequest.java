package com.outsourcingdelivery.domain.store.dto.request;

import com.outsourcingdelivery.domain.storeSchedule.dto.request.CreateStoreScheduleRequest;
import lombok.Getter;

@Getter
public class createStoreAndScheduleRequest {
    private CreateStoreRequest store;
    private CreateStoreScheduleRequest schedule;
}
