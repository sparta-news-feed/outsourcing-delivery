package com.outsourcingdelivery.domain.order.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SnowflakeOrderNoGeneratorTest {

    private SnowflakeOrderNoGenerator orderNoGenerator;

    @BeforeEach
    void setUp() {
        orderNoGenerator = new SnowflakeOrderNoGenerator();
    }

    @Test
    @DisplayName("같은 시간에 생성된 주문번호들이 각각 Unique한지 확인")
    void generateOrderNo() {
        // given
        int sampleSize = 1000;
        Set<Long> uniqueOrderNos = new HashSet<>();

        // when
        for (int i = 0; i < sampleSize; i++) {
            uniqueOrderNos.add(orderNoGenerator.generateOrderNo());
        }

        // then
        assertThat(uniqueOrderNos).hasSize(sampleSize);
    }

    @Test
    @DisplayName("서버 시간이 역행했을 때 예외 발생 확인")
    void generateOrderNo2() throws Exception {
        // given
        orderNoGenerator.generateOrderNo(); // 정상적으로 주문번호를 생성
        mockLastTimestamp(orderNoGenerator, System.currentTimeMillis() + 1000); // 시간 역행 시뮬레이션

        // then
        assertThatThrownBy(() -> orderNoGenerator.generateOrderNo())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Clock moved backwards");
    }

    @Test
    @DisplayName("주문 번호가 타임스탬프에 따라 증가하는지 확인")
    void generateOrderNo3() {
        // given
        long orderNo1 = orderNoGenerator.generateOrderNo();
        long orderNo2 = orderNoGenerator.generateOrderNo();
        long orderNo3 = orderNoGenerator.generateOrderNo();

        // then
        assertThat(orderNo1).isLessThan(orderNo2);
        assertThat(orderNo2).isLessThan(orderNo3);
    }

    private void mockLastTimestamp(SnowflakeOrderNoGenerator generator, long newTimestamp) throws Exception {
        Field field = SnowflakeOrderNoGenerator.class.getDeclaredField("lastTimestamp");
        field.setAccessible(true); // private 필드 접근 가능하도록 설정
        field.setLong(generator, newTimestamp); // 필드 값을 새로운 값으로 설정
    }
}