package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public UserPoint charge(long amount) {
        if (this.point + amount > 1000000L) {
            throw new IllegalStateException("최대 잔고를 넘을 수 없습니다. (최대: 1,000,000)");
        }
        return new UserPoint(this.id, this.point + amount, System.currentTimeMillis());
    }

    public UserPoint use(long amount) {
        if (this.point < amount) {
            throw new IllegalStateException("포인트 부족");
        }
        return new UserPoint(this.id, this.point - amount, System.currentTimeMillis());
    }
}
