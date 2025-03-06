package com.outsourcingdelivery.domain.store.dto.request;

import com.outsourcingdelivery.domain.storeSchedule.dto.request.StoreScheduleRequest;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StoreAndScheduleRequest {
    @Valid
    private StoreRequest store;
    @Valid
    private StoreScheduleRequest schedule;

    @Builder
    private StoreAndScheduleRequest(StoreRequest store, StoreScheduleRequest schedule) {
        this.store = store;
        this.schedule = schedule;
    }
}
