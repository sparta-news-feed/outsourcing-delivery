//package com.outsourcingdelivery.domain.store.controller;
//
//import com.outsourcingdelivery.common.dto.AuthUser;
//import com.outsourcingdelivery.domain.ControllerTestSupport;
//import com.outsourcingdelivery.domain.store.dto.request.StoreAndScheduleRequest;
//import com.outsourcingdelivery.domain.store.dto.request.StoreRequest;
//import com.outsourcingdelivery.domain.storeSchedule.dto.request.StoreScheduleRequest;
//import com.outsourcingdelivery.domain.storeSchedule.enums.DayOfWeek;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalTime;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doNothing;
//
//public class StoreControllerTest extends ControllerTestSupport {
//
//    @DisplayName("가게, 영업시간 생성 - 성공")
//    @Test
//    void createStoreAndSchedule() throws Exception {
//        // given
//        StoreRequest storeRequest = StoreRequest.builder()
//                .storeName("새로운 가게")
//                .minOrderPrice(10000)
//                .phoneNumber("01000000000")
//                .address("가게 주소")
//                .build();
//
//        StoreScheduleRequest scheduleRequest = StoreScheduleRequest.builder()
//                .dayOfWeek(DayOfWeek.MON)
//                .openTime(LocalTime.of(10,00))
//                .closeTime(LocalTime.of(22,00))
//                .build();
//
//        StoreAndScheduleRequest request = StoreAndScheduleRequest.builder()
//                .store(storeRequest)
//                .schedule(scheduleRequest)
//                .build();
//
//        // when
//        doNothing().
//                when(storeService).create(any(AuthUser.class),any(StoreAndScheduleRequest.class), )
//    }
//}
