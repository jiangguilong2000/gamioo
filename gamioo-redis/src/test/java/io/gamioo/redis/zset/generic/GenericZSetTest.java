package io.gamioo.redis.zset.generic;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.LongStream;

/**
 * {@link GenericZSet}的复杂score测试
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/11/7
 * github - https://github.com/hl845740757
 */
public class GenericZSetTest {

    public static void main(String[] args) {
        final GenericZSet<Long, ComplexScore> zSet = GenericZSet.newLongKeyZSet(new ComplexScoreHandler());

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

    private static ComplexScore randomScore() {
        return new ComplexScore(ThreadLocalRandom.current().nextInt(0, 4),
                ThreadLocalRandom.current().nextInt(1, 101),
                System.currentTimeMillis());
    }

    private static class ComplexScore {

        private final int vipLevel;
        private final int level;
        private final long timestamp;

        ComplexScore(int vipLevel, int level, long timeStamp) {
            this.level = level;
            this.vipLevel = vipLevel;
            this.timestamp = timeStamp;
        }

        public int getLevel() {
            return level;
        }

        public int getVipLevel() {
            return vipLevel;
        }

        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return "{" +
                    "vipLevel=" + vipLevel +
                    ", level=" + level +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }

    private static class ComplexScoreHandler implements ScoreHandler<ComplexScore> {

        @Override
        public int compare(ComplexScore o1, ComplexScore o2) {
            // vip等级排序 - 等级高的排前面(逆序)
            final int vipLevelCompareR = Integer.compare(o2.vipLevel, o1.vipLevel);
            if (vipLevelCompareR != 0) {
                return vipLevelCompareR;
            }
            // 普通等级排序 - 等级高的排前面(逆序)
            final int levelCompareR = Integer.compare(o2.level, o1.level);
            if (levelCompareR != 0) {
                return levelCompareR;
            }
            // 时间戳升序(时间戳小的排前面)
            return Long.compare(o1.timestamp, o2.timestamp);
        }

        @Override
        public ComplexScore sum(ComplexScore oldScore, ComplexScore increment) {
            throw new UnsupportedOperationException();
        }
    }
}
