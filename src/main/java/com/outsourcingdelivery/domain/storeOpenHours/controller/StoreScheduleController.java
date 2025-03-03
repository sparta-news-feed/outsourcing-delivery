package com.outsourcingdelivery.domain.storeOpenHours.controller;

import com.outsourcingdelivery.domain.storeOpenHours.service.StoreScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hours")
public class StoreScheduleController {
    private final StoreScheduleService storeScheduleService;
}
