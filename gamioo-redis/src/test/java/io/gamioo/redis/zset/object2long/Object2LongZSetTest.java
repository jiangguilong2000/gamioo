package io.gamioo.redis.zset.object2long;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.LongStream;

/**
 * {@link Object2LongZSet}的复杂score测试
 * 这里的score由vipLevel和level拼接而成，因此在比较时需要拆分。
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/11/7
 * github - https://github.com/hl845740757
 */
public class Object2LongZSetTest {

    private static final long MULTIPLE = 10000;

    public static void main(String[] args) {
        final Object2LongZSet<Long> zSet = Object2LongZSet.newLongKeyZSet(new ComplexScoreHandler());

        // 插入数据
        LongStream.rangeClosed(1, 10000).forEach(playerId -> {
            zSet.zadd(randomScore(), playerId);
        });

        // 覆盖数据
        LongStream.rangeClosed(1, 10000).forEach(playerId -> {
            zSet.zadd(randomScore(), playerId);
        });

        System.out.println("------------------------- dump ----------------------");
        System.out.println(zSet.dump());
        System.out.println();
    }

    private static long randomScore() {
        return composeToLong(ThreadLocalRandom.current().nextInt(0, 4),
                ThreadLocalRandom.current().nextInt(1, 101));
    }

    private static long composeToLong(int a, int b) {
        return (long) a * MULTIPLE + b;
    }

    private static int vipLevel(long score) {
        return (int) (score / MULTIPLE);
    }

    private static int level(long score) {
        return (int) (score % MULTIPLE);
    }

    private static class ComplexScoreHandler implements LongScoreHandler {

        @Override
        public int compare(long o1, long o2) {
            // vip等级排序 - 等级高的排前面(逆序)
            final int vipLevelCompareR = Integer.compare(vipLevel(o2), vipLevel(o1));
            if (vipLevelCompareR != 0) {
                return vipLevelCompareR;
            }
            // 普通等级排序 - 等级高的排前面(逆序)
            return Integer.compare(level(o2), level(o1));
        }

        @Override
        public long sum(long oldScore, long increment) {
            throw new UnsupportedOperationException();
        }
    }
}
