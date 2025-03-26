package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    //  유저 포인트 조회
    public UserPoint getPoint(long id) {
        return userPointTable.selectById(id);
    }

    // 유저 포인트 내역 조회
    public List<PointHistory> getHistory(long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }

    // 유저 포인트 충전
    public UserPoint charge(long id, long amount) {
        //  사용자 ID에 따라 고유한 문자열 생성 : "lock-" + id
        //  같은 id에 대해선 항상 같은 객체 : .intern()
        synchronized (("lock-" + id).intern()) {
            UserPoint userPoint = userPointTable.selectById(id);
            UserPoint chargedPoint = userPoint.charge(amount);

            userPointTable.insertOrUpdate(id, chargedPoint.point());
            pointHistoryTable.insert(id, amount, TransactionType.CHARGE, chargedPoint.updateMillis());

            return chargedPoint;
        }
    }

    // 유저 포인트 사용
    public UserPoint use(long id, long amount) {
        //  사용자 ID에 따라 고유한 문자열 생성 : "lock-" + id
        //  같은 id에 대해선 항상 같은 객체 : .intern()
        synchronized (("lock-" + id).intern()) {
            UserPoint userPoint = userPointTable.selectById(id);
            UserPoint usedPoint = userPoint.use(amount);

            userPointTable.insertOrUpdate(id, usedPoint.point());
            pointHistoryTable.insert(id, amount, TransactionType.USE, usedPoint.updateMillis());

            return usedPoint;
        }
    }
}
