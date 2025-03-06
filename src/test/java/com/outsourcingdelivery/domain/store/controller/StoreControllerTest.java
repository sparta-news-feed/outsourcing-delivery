package com.outsourcingdelivery.domain.store.controller;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.domain.ControllerTestSupport;
import com.outsourcingdelivery.domain.store.dto.request.StoreAndScheduleRequest;
import com.outsourcingdelivery.domain.store.dto.request.StoreRequest;
import com.outsourcingdelivery.domain.storeSchedule.dto.request.StoreScheduleRequest;
import com.outsourcingdelivery.domain.storeSchedule.enums.DayOfWeek;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StoreControllerTest extends ControllerTestSupport {

    @DisplayName("가게, 영업시간 생성 - 성공")
    @Test
    void createStoreAndSchedule() throws Exception {
        // given
        StoreRequest storeRequest = StoreRequest.builder()
                .storeName("새로운 가게")
                .minOrderPrice(10000)
                .phoneNumber("01000000000")
                .address("가게 주소")
                .build();

        StoreScheduleRequest scheduleRequest1 = StoreScheduleRequest.builder()
                .dayOfWeek(DayOfWeek.MON)
                .openTime(LocalTime.of(10,00))
                .closeTime(LocalTime.of(22,00))
                .build();
        StoreScheduleRequest scheduleRequest2 = StoreScheduleRequest.builder()
                .dayOfWeek(DayOfWeek.TUE)
                .openTime(LocalTime.of(10,00))
                .closeTime(LocalTime.of(22,00))
                .build();
        StoreScheduleRequest scheduleRequest3 = StoreScheduleRequest.builder()
                .dayOfWeek(DayOfWeek.WED)
                .openTime(LocalTime.of(10,00))
                .closeTime(LocalTime.of(22,00))
                .build();
        StoreScheduleRequest scheduleRequest4 = StoreScheduleRequest.builder()
                .dayOfWeek(DayOfWeek.THU)
                .openTime(LocalTime.of(10,00))
                .closeTime(LocalTime.of(22,00))
                .build();
        StoreScheduleRequest scheduleRequest5 = StoreScheduleRequest.builder()
                .dayOfWeek(DayOfWeek.FRI)
                .openTime(LocalTime.of(10,00))
                .closeTime(LocalTime.of(22,00))
                .build();
        StoreScheduleRequest scheduleRequest6 = StoreScheduleRequest.builder()
                .dayOfWeek(DayOfWeek.SAT)
                .openTime(LocalTime.of(10,00))
                .closeTime(LocalTime.of(22,00))
                .build();
        StoreScheduleRequest scheduleRequest7 = StoreScheduleRequest.builder()
                .dayOfWeek(DayOfWeek.SUN)
                .openTime(LocalTime.of(10,00))
                .closeTime(LocalTime.of(22,00))
                .build();

        List<StoreScheduleRequest> scheduleRequests = new ArrayList<>();
        scheduleRequests.add(0, scheduleRequest1);
        scheduleRequests.add(1, scheduleRequest2);
        scheduleRequests.add(2, scheduleRequest3);
        scheduleRequests.add(3, scheduleRequest4);
        scheduleRequests.add(4, scheduleRequest5);
        scheduleRequests.add(5, scheduleRequest6);
        scheduleRequests.add(6, scheduleRequest7);

        StoreAndScheduleRequest request = StoreAndScheduleRequest.builder()
                .store(storeRequest)
                .schedules(scheduleRequests)
                .build();

        // when
        when(storeService.createStoreAndSchedule(any(AuthUser.class), any(StoreAndScheduleRequest.class)))
            .thenReturn("가게 및 영업시간 생성에 성공했습니다.");

        // then
        mockMvc.perform(post("/api/v1/stores")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("가게 및 영업시간 생성에 성공했습니다."));
    }
}
