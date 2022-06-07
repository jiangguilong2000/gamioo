/*
 *  Copyright 2019 wjybxx
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to iBn writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.gamioo.redis.zset;

import java.util.concurrent.ThreadLocalRandom;

/**
 * ZSet工具类，提取公共代码和常量
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/11/26
 */
public class ZSetUtils {

    /**
     * 成员id到成员分数的映射初始容量，减少不必要的扩容。
     */
    public static final int INIT_CAPACITY = 128;
    /**
     * 跳表允许最大层级
     */
    public static final int ZSKIPLIST_MAXLEVEL = 32;
    /**
     * 跳表升层概率
     */
    private static final float ZSKIPLIST_P = 0.25f;

    /**
     * 转换起始排名
     *
     * @param start     请求参数中的起始排名(0-based)
     * @param zslLength 跳表的长度
     * @return 有效起始排名
     */
    public static int convertStartRank(int start, int zslLength) {
        if (start < 0) {
            start += zslLength;
        }
        if (start < 0) {
            start = 0;
        }
        return start;
    }

    /**
     * 转换截止排名
     *
     * @param end       请求参数中的截止排名(0-based)
     * @param zslLength 跳表的长度
     * @return 有效截止排名
     */
    public static int convertEndRank(int end, int zslLength) {
        if (end < 0) {
            end += zslLength;
        }
        if (end >= zslLength) {
            end = zslLength - 1;
        }
        return end;
    }

    /**
     * 判断排名区间是否为空
     *
     * @param start     转换后的起始排名(0-based)
     * @param end       转换后的截止排名(0-based)
     * @param zslLength 跳表长度
     * @return true/false
     */
    public static boolean isRankRangeEmpty(final int start, final int end, final int zslLength) {
        /* Invariant: start >= 0, so this test will be true when end < 0.
         * The range is empty when start > end or start >= length. */
        return start > end || start >= zslLength;
    }

    /**
     * 返回一个随机的层级分配给即将插入的节点。
     * 返回的层级值在 1 和 ZSKIPLIST_MAXLEVEL 之间（包含两者）。
     * 具有类似幂次定律的分布，越高level返回的可能性更小。
     * <p>
     * Returns a random level for the new skiplist node we are going to create.
     * The return value of this function is between 1 and ZSKIPLIST_MAXLEVEL
     * (both inclusive), with a powerlaw-alike distribution where higher
     * levels are less likely to be returned.
     *
     * @return level
     */
    public static int zslRandomLevel() {
        int level = 1;
        while (level < ZSKIPLIST_MAXLEVEL && ThreadLocalRandom.current().nextFloat() < ZSKIPLIST_P) {
            level++;
        }
        return level;
    }

    /**
     * 释放update引用的对象
     *
     * @param update     更新数组
     * @param realLength 实际长度
     */
    public static void releaseUpdate(Object[] update, int realLength) {
        for (int index = 0; index < realLength; index++) {
            update[index] = null;
        }
    }

    /**
     * 重置rank中的数据
     *
     * @param rank       范围
     * @param realLength 实际长度
     */
    public static void releaseRank(int[] rank, int realLength) {
        for (int index = 0; index < realLength; index++) {
            rank[index] = 0;
        }
    }
}
