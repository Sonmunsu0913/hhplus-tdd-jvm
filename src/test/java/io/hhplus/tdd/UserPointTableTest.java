package io.hhplus.tdd;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserPointTableTest {

    UserPointTable userPointTable;

    @BeforeEach
    void setUp() {
        userPointTable = new UserPointTable();
    }

    // 포인트가 없는 유저 조회
    @Test
    void selectById_1() {
        // given
        long userId = 100L;

        // when
        UserPoint point = userPointTable.selectById(userId);

        // then
        assertEquals(userId, point.id());
        assertEquals(0L, point.point());
    }

    // 새 유저 포인트 등록
    @Test
    void insertOrUpdate_1() {
        // given
        long userId = 1L;
        long amount = 1000L;

        // when
        UserPoint saved = userPointTable.insertOrUpdate(userId, amount);

        // then
        assertEquals(userId, saved.id());
        assertEquals(amount, saved.point());

        // when
        UserPoint fetched = userPointTable.selectById(userId);

        // then
        assertEquals(amount, fetched.point());
    }

    // 기존 유저 포인트 덮어쓰기
    @Test
    void insertOrUpdate_2() {
        // given
        long userId = 1L;
        userPointTable.insertOrUpdate(userId, 500L);

        // when
        UserPoint updated = userPointTable.insertOrUpdate(userId, 2000L);

        // then
        assertEquals(2000L, updated.point());

        UserPoint fetched = userPointTable.selectById(userId);
        assertEquals(2000L, fetched.point());
    }
}
