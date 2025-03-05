package com.outsourcingdelivery.domain.store.dto.request;

import com.outsourcingdelivery.domain.storeSchedule.dto.request.UpdateStoreScheduleRequest;
import jakarta.validation.Valid;
import lombok.Getter;

@Getter
public class UpdateStoreAndScheduleRequest {
    @Valid
    private UpdateStoreRequest store;
    @Valid
    private UpdateStoreScheduleRequest schedule;
}
