package io.gamioo.redis.zset.generic;

/**
 * {@link GenericZSet}中“score”范围描述信息 - specification模式
 */
public class ZScoreRangeSpec<S> {
    /**
     * 最低分数
     */
    public final S min;
    /**
     * 是否去除最低分
     * exclusive
     */
    public final boolean minex;
    /**
     * 最高分数
     */
    public final S max;
    /**
     * 是否去除最高分
     * exclusive
     */
    public final boolean maxex;

    public ZScoreRangeSpec(S min, boolean minex, S max, boolean maxex) {
        this.min = min;
        this.minex = minex;
        this.max = max;
        this.maxex = maxex;
    }
}
