package com.outsourcingdelivery.domain.store.dto.request;

import com.outsourcingdelivery.domain.storeSchedule.dto.request.StoreScheduleRequest;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class StoreAndScheduleRequest {
    @Valid
    private StoreRequest store;
    @Valid
    private List<StoreScheduleRequest> schedules;

    @Builder
    private StoreAndScheduleRequest(StoreRequest store, List<StoreScheduleRequest> schedules) {
        this.store = store;
        this.schedules = schedules;
    }
}
