package io.hhplus.tdd;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PointServiceTest {

    private PointService pointService;
    private UserPointTable userPointTable;
    private PointHistoryTable pointHistoryTable;

    @BeforeEach
    void setUp() {
        userPointTable = new UserPointTable();
        pointHistoryTable = new PointHistoryTable();
        pointService = new PointService(userPointTable, pointHistoryTable);
    }

    @Test
    void getPoint_포인트_없는_유저_확인() {
        // given
        long userId = 100L;

        // when
        UserPoint point = pointService.getPoint(userId);

        // then
        assertEquals(userId, point.id());
        assertEquals(0L, point.point());
    }

    @Test
    void charge_포인트_충전_후_내역_확인() {
        // given
        long userId = 1L;
        long chargeAmount = 1000L;

        // when
        UserPoint charged = pointService.charge(userId, chargeAmount);

        // then
        assertEquals(1000L, charged.point());

        List<PointHistory> histories = pointService.getHistory(userId);
        assertEquals(1, histories.size());
        assertEquals(TransactionType.CHARGE, histories.get(0).type());
        assertEquals(1000L, histories.get(0).amount());
    }

    @Test
    void use_포인트_사용_후_내역_확인() {
        // given
        long userId = 1L;
        pointService.charge(userId, 1000L);

        // when
        UserPoint used = pointService.use(userId, 400L);

        // then
        assertEquals(600L, used.point());

        List<PointHistory> histories = pointService.getHistory(userId);
        assertEquals(2, histories.size()); // CHARGE + USE
        assertTrue(histories.stream().anyMatch(h -> h.type() == TransactionType.USE));
    }

    @Test
    void charge_최대_잔고_제한() {
        // given
        long userId = 1L;
        long chargeAmount1 = 1000000L;
        long chargeAmount2 = 1000L;

        pointService.charge(userId, chargeAmount1);

        // when & then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            pointService.charge(userId, chargeAmount2);
        });
        assertEquals("최대 잔고를 넘을 수 없습니다. (최대: 1,000,000)", ex.getMessage());
    }

    @Test
    void use_포인트_부족_예외() {
        // given
        long userId = 1L;
        pointService.charge(userId, 500L);

        // when & then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            pointService.use(userId, 600L);
        });
        assertEquals("포인트 부족", ex.getMessage());
    }
}
