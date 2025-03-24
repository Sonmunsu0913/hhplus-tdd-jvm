package io.hhplus.tdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PointHistoryTableTest {

    PointHistoryTable table;

    //  테스트 할 때마다 객체 초기화
    @BeforeEach
    void setUp() {
        table = new PointHistoryTable();
    }

    //  유저의 내역 저장 확인
    @Test
    void insert_1() {
        PointHistory history = table.insert(1L, 1000L, TransactionType.CHARGE, System.currentTimeMillis());

        assertEquals(1L, history.userId());
        assertEquals(1000L, history.amount());
        assertEquals(TransactionType.CHARGE, history.type());
    }

    //  유저의 내역 확인
    @Test
    void selectAllByUserId_1() {
        long now = System.currentTimeMillis();
        table.insert(1L, 1000L, TransactionType.CHARGE, now);
        table.insert(1L, 500L, TransactionType.USE, now);
        table.insert(2L, 300L, TransactionType.CHARGE, now);

        List<PointHistory> histories = table.selectAllByUserId(1L);

        assertEquals(2, histories.size());
        assertTrue(histories.stream().allMatch(h -> h.userId() == 1L));
    }
}