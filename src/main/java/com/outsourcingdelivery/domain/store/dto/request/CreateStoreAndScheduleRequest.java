package com.outsourcingdelivery.domain.store.dto.request;

import com.outsourcingdelivery.domain.storeSchedule.dto.request.CreateStoreScheduleRequest;
import jakarta.validation.Valid;
import lombok.Getter;

@Getter
public class CreateStoreAndScheduleRequest {
    @Valid
    private CreateStoreRequest store;
    @Valid
    private CreateStoreScheduleRequest schedule;
}
