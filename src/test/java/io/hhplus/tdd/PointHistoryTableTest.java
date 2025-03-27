package io.hhplus.tdd;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PointHistoryTableTest {

    PointHistoryTable table;

    @BeforeEach
    void setUp() {
        table = new PointHistoryTable();
    }

    // 유저의 포인트 이력 저장 확인
    @Test
    void insert_1() {
        // given
        long userId = 1L;
        long amount = 1000L;
        TransactionType type = TransactionType.CHARGE;
        long now = System.currentTimeMillis();

        // when
        PointHistory history = table.insert(userId, amount, type, now);

        // then
        assertEquals(userId, history.userId());
        assertEquals(amount, history.amount());
        assertEquals(type, history.type());
    }

    // 유저별 포인트 이력 조회
    @Test
    void selectAllByUserId_1() {
        // given
        long now = System.currentTimeMillis();
        table.insert(1L, 1000L, TransactionType.CHARGE, now);
        table.insert(1L, 500L, TransactionType.USE, now);
        table.insert(2L, 300L, TransactionType.CHARGE, now);

        // when
        List<PointHistory> histories = table.selectAllByUserId(1L);

        // then
        assertEquals(2, histories.size());
        assertTrue(histories.stream().allMatch(h -> h.userId() == 1L));
    }
}
