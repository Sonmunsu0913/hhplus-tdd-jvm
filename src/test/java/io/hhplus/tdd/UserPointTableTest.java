package io.hhplus.tdd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserPointTableTest {

    UserPointTable userPointTable;

    //  테스트 할 때마다 객체 초기화
    @BeforeEach
    void setUp() {
        userPointTable = new UserPointTable();
    }

    //  포인트가 없는 유저 확인
    @Test
    void selectById_1() {
        UserPoint point = userPointTable.selectById(100L);
        assertEquals(100L, point.id());
        assertEquals(0L, point.point());
    }

    //  유저 포인트 충전 확인(0에서 충전)
    @Test
    void insertOrUpdate_1() {
        UserPoint saved = userPointTable.insertOrUpdate(1L, 1000L);
        assertEquals(1L, saved.id());
        assertEquals(1000L, saved.point());

        UserPoint fetched = userPointTable.selectById(1L);
        assertEquals(1000L, fetched.point());
    }

    //  유저 포인트 충전 확인2(포인트가 있을 때 추가 충전)
    @Test
    void insertOrUpdate_2() {
        userPointTable.insertOrUpdate(1L, 500L);
        UserPoint updated = userPointTable.insertOrUpdate(1L, 2000L);

        assertEquals(2000L, updated.point());

        UserPoint fetched = userPointTable.selectById(1L);
        assertEquals(2000L, fetched.point());
    }
}
