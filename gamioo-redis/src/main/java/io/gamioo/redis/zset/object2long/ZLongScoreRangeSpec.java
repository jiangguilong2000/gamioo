package io.gamioo.redis.zset.object2long;

/**
 * {@link Object2LongZSet}中“score”范围描述信息 - specification模式
 */
public final class ZLongScoreRangeSpec {
    /**
     * 最低分数
     */
    final long min;
    /**
     * 是否去除下限
     * exclusive
     */
    final boolean minex;
    /**
     * 最高分数
     */
    final long max;
    /**
     * 是否去除上限
     * exclusive
     */
    final boolean maxex;

    public ZLongScoreRangeSpec(long min, boolean minex, long max, boolean maxex) {
        this.min = min;
        this.max = max;
        this.minex = minex;
        this.maxex = maxex;
    }
}
