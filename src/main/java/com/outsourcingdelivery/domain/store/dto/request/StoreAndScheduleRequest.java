package com.outsourcingdelivery.domain.store.dto.request;

import com.outsourcingdelivery.domain.storeSchedule.dto.request.StoreScheduleRequest;
import jakarta.validation.Valid;
import lombok.Getter;

@Getter
public class StoreAndScheduleRequest {
    @Valid
    private StoreRequest store;
    @Valid
    private StoreScheduleRequest schedule;
}
