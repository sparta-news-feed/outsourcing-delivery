package com.outsourcingdelivery.domain.store.dto.request;

import com.outsourcingdelivery.domain.storeSchedule.dto.request.StoreScheduleRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StoreAndScheduleRequest {
    @Valid
    @NotNull
    private StoreRequest store;
    @Valid
    @NotNull
    private List<StoreScheduleRequest> schedules;

    @Builder
    private StoreAndScheduleRequest(StoreRequest store, List<StoreScheduleRequest> schedules) {
        this.store = store;
        this.schedules = schedules;
    }
}
