package com.outsourcingdelivery.common.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SnowflakeOrderNoGeneratorTest {

    @BeforeEach
    void resetState() throws Exception {
        // 테스트 실행 시 static 변수 초기화 (lastTimestamp, sequence)
        resetStaticField("lastTimestamp", -1L);
        resetStaticField("sequence", 0L);
    }

    private void resetStaticField(String fieldName, long value) throws Exception {
        Field field = SnowflakeOrderNoGenerator.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.setLong(null, value);
    }

    private void mockLastTimestamp(long newTimestamp) throws Exception {
        resetStaticField("lastTimestamp", newTimestamp);
    }

    @Test
    @DisplayName("같은 시간에 생성된 주문번호들이 각각 Unique한지 확인")
    void generateOrderNo() {
        // given
        int sampleSize = 1000;
        Set<Long> uniqueOrderNos = new HashSet<>();

        // when
        for (int i = 0; i < sampleSize; i++) {
            uniqueOrderNos.add(SnowflakeOrderNoGenerator.generateOrderNo());
        }

        // then
        assertThat(uniqueOrderNos).hasSize(sampleSize);
    }

    @Test
    @DisplayName("서버 시간이 역행했을 때 예외 발생 확인")
    void generateOrderNo2() throws Exception {
        // given
        SnowflakeOrderNoGenerator.generateOrderNo(); // 정상적으로 주문번호를 생성
        mockLastTimestamp(System.currentTimeMillis() + 1000); // 시간 역행 시뮬레이션

        // then
        assertThatThrownBy(SnowflakeOrderNoGenerator::generateOrderNo)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Clock moved backwards");
    }

    @Test
    @DisplayName("주문 번호가 타임스탬프에 따라 증가하는지 확인")
    void generateOrderNo3() {
        // given
        long orderNo1 = SnowflakeOrderNoGenerator.generateOrderNo();
        long orderNo2 = SnowflakeOrderNoGenerator.generateOrderNo();
        long orderNo3 = SnowflakeOrderNoGenerator.generateOrderNo();

        // then
        assertThat(orderNo1).isLessThan(orderNo2);
        assertThat(orderNo2).isLessThan(orderNo3);
    }
}