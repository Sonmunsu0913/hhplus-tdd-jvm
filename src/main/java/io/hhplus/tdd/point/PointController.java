package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public PointController(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public UserPoint point(@PathVariable long id) {
        return userPointTable.selectById(id);
//        return new UserPoint(0, 0, 0);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(@PathVariable long id) {
        return pointHistoryTable.selectAllByUserId(id);
//        return List.of();
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(@PathVariable long id, @RequestBody long amount) {
        UserPoint userPoint = userPointTable.selectById(id);
        long chargedAmount = userPoint.point() + amount;
        UserPoint charged = userPointTable.insertOrUpdate(id, chargedAmount);

        pointHistoryTable.insert(id, chargedAmount, TransactionType.CHARGE, charged.updateMillis());
        return charged;
//        return new UserPoint(0, 0, 0);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(@PathVariable long id, @RequestBody long amount) {
        UserPoint userPoint = userPointTable.selectById(id);
        if (userPoint.point() < amount) {
            throw new IllegalStateException("포인트 부족");
        }

        long usedAmount = userPoint.point() - amount;
        UserPoint used = userPointTable.insertOrUpdate(id, usedAmount);

        pointHistoryTable.insert(id, usedAmount, TransactionType.USE, used.updateMillis());
        return used;
        //        return new UserPoint(0, 0, 0);
    }
}
