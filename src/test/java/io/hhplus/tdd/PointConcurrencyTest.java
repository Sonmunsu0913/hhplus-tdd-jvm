package io.hhplus.tdd;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointConcurrencyTest {

    private PointService pointService;

    @BeforeEach
    void setUp() {
        pointService = new PointService(new UserPointTable(), new PointHistoryTable());
    }

    @Test
    void 동시에_충전해도_정상_합산() throws InterruptedException {
        // given
        long userId = 1L;
        int threadCount = 10;
        long chargeAmount = 1_000L;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when: 10개의 스레드가 동시에 1,000원씩 충전
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.charge(userId, chargeAmount);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 작업 완료 대기

        // then: 10 * 1,000 = 10,000 포인트가 되어야 함
        UserPoint result = pointService.getPoint(userId);
        assertEquals(10_000L, result.point());
    }
}
