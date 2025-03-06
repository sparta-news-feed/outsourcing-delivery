package com.outsourcingdelivery.common.utils;

public class SnowflakeOrderNoGenerator {
    private static final long epoch = 1740787200000L;  // 2025-03-01 00:00:00 UTC
    private static final long machineId = 0L;  // 단일 서버
    private static final long maxSequence = 4095;  // 같은 밀리초에서 4096개 ID 생성 가능

    private static long lastTimestamp = -1L;   // 마지막으로 생성한 타임 스탬프
    private static long sequence = 0L; // 같은 밀리초에서 증가하는 시퀀스 값

    public static synchronized long generateOrderNo() {
        long currentTimestamp = System.currentTimeMillis();

        if (currentTimestamp < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards. Refusing to generate ID");
        }

        // 같은 밀리초인 경우 sequence 값 증가
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                while (currentTimestamp <= lastTimestamp) {
                    currentTimestamp = System.currentTimeMillis();
                }
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = currentTimestamp;

        // 순차적이고 유일한 64비트 정수형 ID 생성
        return ((currentTimestamp - epoch) << 22) | (machineId << 12) | sequence;
    }
}
