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

package io.gamioo.redis.zset.long2object;


import io.gamioo.redis.zset.ZSetUtils;
import io.gamioo.redis.zset.generic.Entry;
import io.gamioo.redis.zset.generic.ScoreHandler;
import io.gamioo.redis.zset.generic.ScoreRangeSpec;
import io.gamioo.redis.zset.generic.ZScoreRangeSpec;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongComparators;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;

import static io.gamioo.redis.zset.ZSetUtils.ZSKIPLIST_MAXLEVEL;

/**
 * key为long类型，score为泛型类型的sorted set - 参考redis的zset实现
 * <b>排序规则</b>
 * 有序集合里面的成员是不能重复的，都是唯一的，但是，不同成员间有可能有相同的分数。
 * 当多个成员有相同的分数时，它们将按照键排序。
 * 即：分数作为第一排序条件，键作为第二排序条件，当分数相同时，比较键的大小。
 * <p>
 * <b>NOTE</b>：
 * 1. ZSET中的排名从0开始（提供给用户的接口，排名都从0开始）
 * <p>
 * 2. 我们允许zset中的成员是降序排列的-{@link ScoreHandler}决定，可以更好的支持根据score降序的排行榜，
 * 而不是强迫你总是调用反转系列接口{@code zrev...}，那样的设计不符合人的正常思维，就很容易出错。
 * <p>
 * 3. 我们修改了redis中根据min和max查找和删除成员的接口，修改为start和end，当根据score范围查找或删除元素时，并不要求start小于等于end，我们会处理它们的大小关系。<br>
 * Q: 为什么要这么改动呢？<br>
 * A: 举个栗子：假如ScoreHandler比较两个long类型的score是逆序的，现在要删除排行榜中 1-10000分的成员，如果方法告诉你要传入的的是min和max，
 * 你会很自然的传入想到 (1,10000) 而不是 (10000,1)。因此，如果接口不做调整，这个接口就太反人类了，谁用都得错。
 * <p>
 * 4. score泛型化以后，有一些注意事项，请查看{@link ScoreHandler}中关于score的注意事项。
 *
 * <p>
 * 这里只实现了redis zset中的常用的接口，扩展不是太麻烦，可以自己根据需要实现。
 *
 * @param <S> the type of score
 * @author wjybxx
 * @version 1.0
 * date - 2019/11/4
 */
@NotThreadSafe
public class Long2ObjectZSet<S> implements Iterable<Long2ObjectEntry<S>> {

    /**
     * member -> score
     */
    private final Long2ObjectMap<S> dict = new Long2ObjectOpenHashMap<>(ZSetUtils.INIT_CAPACITY);
    private final SkipList<S> zsl;

    private Long2ObjectZSet(LongComparator objComparator, ScoreHandler<S> scoreHandler) {
        this.zsl = new SkipList<>(objComparator, scoreHandler);
    }

    /**
     * 创建一个键为long类型的zset
     *
     * @param scoreHandler 分数处理器
     * @param <S>          score类型
     * @return zset
     */
    public static <S> Long2ObjectZSet<S> newZSet(ScoreHandler<S> scoreHandler) {
        return new Long2ObjectZSet<>(LongComparators.NATURAL_COMPARATOR, scoreHandler);
    }

    /**
     * 创建一个自定义键类型的zset
     *
     * @param objComparator 键比较器，当score比较结果相等时，比较key。
     * @param scoreHandler  分数处理器
     * @param <S>           score的类型
     * @return zset
     */
    public static <S> Long2ObjectZSet<S> newZSet(LongComparator objComparator, ScoreHandler<S> scoreHandler) {
        return new Long2ObjectZSet<>(objComparator, scoreHandler);
    }
    // -------------------------------------------------------- insert -----------------------------------------------

    /**
     * 往有序集合中新增一个成员。
     * 如果指定添加的成员已经是有序集合里面的成员，则会更新成员的分数（score）并更新到正确的排序位置。
     *
     * @param score  数据的评分
     * @param member 成员id
     */
    public void zadd(final S score, final long member) {
        Objects.requireNonNull(score);
        final S oldScore = dict.put(member, score);
        if (oldScore != null) {
            // Q: 为何不再判断分数相等？
            // A: 这里假定分数相等的情况很少出现，可减少大量无用的判断
            zsl.zslDelete(oldScore, member);
        }
        zsl.zslInsert(score, member);
    }

    /**
     * 往有序集合中新增一个成员。当且仅当该成员不在有序集合时才添加。
     *
     * @param score  数据的评分
     * @param member 成员id
     * @return 添加成功则返回true，否则返回false。
     */
    public boolean zaddnx(final S score, final long member) {
        Objects.requireNonNull(score);
        final S oldScore = dict.putIfAbsent(member, score);
        if (oldScore == null) {
            zsl.zslInsert(score, member);
            return true;
        }
        return false;
    }

    /**
     * 为有序集的成员member的score值加上增量increment，并更新到正确的排序位置。
     * 如果有序集中不存在member，就在有序集中添加一个member，score是increment（就好像它之前的score是0）
     *
     * @param increment 自定义增量
     * @param member    成员id
     * @return 更新后的值
     */
    public S zincrby(S increment, long member) {
        Objects.requireNonNull(increment);
        final S oldScore = dict.get(member);
        final S score = oldScore == null ? increment : zsl.sum(oldScore, increment);
        zadd(score, member);
        return score;
    }

    /**
     * 为有序集的成员member的score值加上增量increment，并更新到正确的排序位置。
     * 如果有序集中不存在member，则放弃更新并返回null。
     *
     * @param increment 自定义增量
     * @param member    成员id
     * @return 更新后的值，如果更新失败，则返回null。
     */
    public S zincrbyxx(S increment, long member) {
        Objects.requireNonNull(increment);
        final S oldScore = dict.get(member);
        if (oldScore == null) {
            return null;
        }

        final S score = zsl.sum(oldScore, increment);
        zadd(score, member);
        return score;
    }

    // -------------------------------------------------------- remove -----------------------------------------------

    /**
     * 删除指定成员
     *
     * @param member 成员id
     * @return 如果成员存在，则返回对应的score，否则返回null。
     */
    public S zrem(long member) {
        final S oldScore = dict.remove(member);
        if (oldScore != null) {
            zsl.zslDelete(oldScore, member);
            return oldScore;
        } else {
            return null;
        }
    }

    // region 通过score删除成员

    /**
     * 移除zset中所有score值介于start和end之间(包括等于start或end)的成员
     *
     * @param start 起始分数 inclusive
     * @param end   截止分数 inclusive
     * @return 删除的成员数目
     */
    public int zremrangeByScore(S start, S end) {
        return zremrangeByScore(zsl.newRangeSpec(start, end));
    }

    /**
     * 移除zset中所有score值在范围区间的成员
     *
     * @param spec score范围区间
     * @return 删除的成员数目
     */
    private int zremrangeByScore(@Nonnull ScoreRangeSpec<S> spec) {
        return zremrangeByScore(zsl.newRangeSpec(spec));
    }

    /**
     * 移除zset中所有score值在范围区间的成员
     *
     * @param spec score范围区间
     * @return 删除的成员数目
     */
    private int zremrangeByScore(@Nonnull ZScoreRangeSpec<S> spec) {
        return zsl.zslDeleteRangeByScore(spec, dict);
    }
    // endregion

    // region 通过排名删除成员

    /**
     * 删除并返回有序集合中的第一个成员。
     * - 不使用min和max，是因为score的比较方式是用户自定义的。
     *
     * @return 如果不存在，则返回null
     */
    @Nullable
    public Long2ObjectEntry<S> zpopFirst() {
        return zremByRank(0);
    }

    /**
     * 删除并返回有序集合中的最后一个成员。
     * - 不使用min和max，是因为score的比较方式是用户自定义的。
     *
     * @return 如果不存在，则返回null
     */
    @Nullable
    public Long2ObjectEntry<S> zpopLast() {
        return zremByRank(zsl.length() - 1);
    }

    /**
     * 删除指定排名的成员
     *
     * @param rank 排名 0-based
     * @return 删除成功则返回该排名对应的数据，否则返回null
     */
    @Nullable
    public Long2ObjectEntry<S> zremByRank(int rank) {
        if (rank < 0 || rank >= zsl.length()) {
            return null;
        }
        final SkipListNode<S> delete = zsl.zslDeleteByRank(rank + 1, dict);
        assert null != delete;
        return new ZSetEntry<>(delete.obj, delete.score);
    }

    /**
     * 删除指定排名范围的全部成员，start和end都是从0开始的。
     * 排名0表示分数最小的成员。
     * start和end都可以是负数，此时它们表示从最高排名成员开始的偏移量，eg: -1表示最高排名的成员， -2表示第二高分的成员，以此类推。
     * <p>
     * <b>Time complexity:</b> O(log(N))+O(M) with N being the number of elements in the sorted set
     * and M the number of elements removed by the operation
     *
     * @param start 起始排名
     * @param end   截止排名
     * @return 删除的成员数目
     */
    public int zremrangeByRank(int start, int end) {
        final int zslLength = zsl.length();

        start = ZSetUtils.convertStartRank(start, zslLength);
        end = ZSetUtils.convertEndRank(end, zslLength);

        if (ZSetUtils.isRankRangeEmpty(start, end, zslLength)) {
            return 0;
        }

        return zsl.zslDeleteRangeByRank(start + 1, end + 1, dict);
    }

    // endregion

    // region 限制成员数量

    /**
     * 删除zset中尾部多余的成员，将zset中的成员数量限制到count之内。
     * 保留前面的count个数成员
     *
     * @param count 剩余数量限制
     * @return 删除的成员数量
     */
    public int zlimit(int count) {
        if (zsl.length() <= count) {
            return 0;
        }
        return zsl.zslDeleteRangeByRank(count + 1, zsl.length(), dict);
    }

    /**
     * 删除zset中头部多余的成员，将zset中的成员数量限制到count之内。
     * - 保留后面的count个数成员
     *
     * @param count 剩余数量限制
     * @return 删除的成员数量
     */
    public int zrevlimit(int count) {
        if (zsl.length() <= count) {
            return 0;
        }
        return zsl.zslDeleteRangeByRank(1, zsl.length() - count, dict);
    }
    // endregion

    // -------------------------------------------------------- query -----------------------------------------------

    /**
     * 返回有序集成员member的score值。
     * 如果member成员不是有序集的成员，返回null - 这里返回任意的基础值都是不合理的，因此必须返回null。
     *
     * @param member 成员id
     * @return score
     */
    public S zscore(long member) {
        return dict.get(member);
    }

    
    public boolean contain(long member) {
        return dict.containsKey(member);
    }

    /**
     * 返回有序集中成员member的排名。
     * <p>
     * <b>Time complexity:</b> O(log(N))
     * <p>
     * <b>与redis的区别</b>：我们使用-1表示成员不存在，而不是返回null。
     *
     * @param member 成员id
     * @return 如果存在该成员，则返回该成员的排名(0-based)，否则返回-1
     */
    public int zrank(long member) {
        final S score = dict.get(member);
        if (score == null) {
            return -1;
        }
        // 0 < zslGetRank <= size
        return zsl.zslGetRank(score, member) - 1;
    }

    /**
     * 返回有序集中成员member的逆序排名。
     * <p>
     * <b>Time complexity:</b> O(log(N))
     * <p>
     * <b>与redis的区别</b>：我们使用-1表示成员不存在，而不是返回null。
     *
     * @param member 成员id
     * @return 如果存在该成员，则返回该成员的排名(0-based)，否则返回-1
     */
    public int zrevrank(long member) {
        final S score = dict.get(member);
        if (score == null) {
            return -1;
        }
        // 0 < zslGetRank <= size
        return zsl.length() - zsl.zslGetRank(score, member);
    }

    /**
     * 获取指定排名的成员数据。
     *
     * @param rank 排名 0-based
     * @return memver，如果不存在，则返回null
     */
    public Long2ObjectEntry<S> zmemberByRank(int rank) {
        if (rank < 0 || rank >= zsl.length()) {
            return null;
        }
        final SkipListNode<S> node = zsl.zslGetElementByRank(rank + 1);
        assert null != node;
        return new ZSetEntry<>(node.obj, node.score);
    }

    /**
     * 获取指定逆序排名的成员数据。
     *
     * @param rank 排名 0-based
     * @return memver，如果不存在，则返回null
     */
    public Long2ObjectEntry<S> zrevmemberByRank(int rank) {
        if (rank < 0 || rank >= zsl.length()) {
            return null;
        }
        final SkipListNode<S> node = zsl.zslGetElementByRank(zsl.length() - rank);
        assert null != node;
        return new ZSetEntry<>(node.obj, node.score);
    }

    // region 通过分数查询

    /**
     * 返回有序集合中的分数在start和end之间的所有成员（包括分数等于start或者end的成员）。
     *
     * @param start 起始分数 inclusive
     * @param end   截止分数 inclusive
     * @return memberInfo
     */
    public List<Long2ObjectEntry<S>> zrangeByScore(S start, S end) {
        return zrangeByScoreWithOptions(zsl.newRangeSpec(start, end), 0, -1, false);
    }

    /**
     * 返回有序集合中的分数在指定范围区间的所有成员。
     *
     * @param spec 范围描述信息
     * @return memberInfo
     */
    public List<Long2ObjectEntry<S>> zrangeByScore(ScoreRangeSpec<S> spec) {
        return zrangeByScoreWithOptions(zsl.newRangeSpec(spec), 0, -1, false);
    }

    /**
     * 返回有序集合中的分数在start和end之间的所有成员（包括分数等于start或者end的成员），返回的成员按照逆序排列。
     *
     * @param start 起始分数 inclusive
     * @param end   截止分数 inclusive
     * @return memberInfo
     */
    public List<Long2ObjectEntry<S>> zrevrangeByScore(final S start, final S end) {
        return zrangeByScoreWithOptions(zsl.newRangeSpec(start, end), 0, -1, true);
    }

    /**
     * 返回有序集合中的分数在指定范围之间的所有成员，返回的成员按照逆序排列。
     *
     * @param rangeSpec score范围区间
     * @return 删除的成员数目
     */
    public List<Long2ObjectEntry<S>> zrevrangeByScore(ScoreRangeSpec<S> rangeSpec) {
        return zrangeByScoreWithOptions(zsl.newRangeSpec(rangeSpec), 0, -1, true);
    }

    /**
     * 返回zset中指定分数区间内的成员，并按照指定顺序返回
     *
     * @param rangeSpec score范围描述信息
     * @param offset    偏移量(用于分页)  大于等于0
     * @param limit     返回的成员数量(用于分页) 小于0表示不限制
     * @param reverse   是否逆序
     * @return memberInfo
     */
    public List<Long2ObjectEntry<S>> zrangeByScoreWithOptions(final ScoreRangeSpec<S> rangeSpec, int offset, int limit, boolean reverse) {
        return zrangeByScoreWithOptions(zsl.newRangeSpec(rangeSpec), offset, limit, reverse);
    }

    /**
     * 返回zset中指定分数区间内的成员，并按照指定顺序返回
     *
     * @param range   score范围描述信息
     * @param offset  偏移量(用于分页)  大于等于0
     * @param limit   返回的成员数量(用于分页) 小于0表示不限制
     * @param reverse 是否逆序
     * @return memberInfo
     */
    private List<Long2ObjectEntry<S>> zrangeByScoreWithOptions(final ZScoreRangeSpec<S> range, int offset, int limit, boolean reverse) {
        if (offset < 0) {
            throw new IllegalArgumentException("offset" + ": " + offset + " (expected: >= 0)");
        }

        SkipListNode<S> listNode;
        /* If reversed, get the last node in range as starting point. */
        if (reverse) {
            listNode = zsl.zslLastInRange(range);
        } else {
            listNode = zsl.zslFirstInRange(range);
        }

        /* No "first" element in the specified interval. */
        if (listNode == null) {
            return new ArrayList<>();
        }

        /* If there is an offset, just traverse the number of elements without
         * checking the score because that is done in the next loop. */
        while (listNode != null && offset-- != 0) {
            if (reverse) {
                listNode = listNode.backward;
            } else {
                listNode = listNode.levelInfo[0].forward;
            }
        }

        final List<Long2ObjectEntry<S>> result = new ArrayList<>();

        /* 这里使用 != 0 判断，当limit小于0时，表示不限制 */
        while (listNode != null && limit-- != 0) {
            /* Abort when the node is no longer in range. */
            if (reverse) {
                if (!zsl.zslValueGteMin(listNode.score, range)) {
                    break;
                }
            } else {
                if (!zsl.zslValueLteMax(listNode.score, range)) {
                    break;
                }
            }

            result.add(new ZSetEntry<>(listNode.obj, listNode.score));

            /* Move to next node */
            if (reverse) {
                listNode = listNode.backward;
            } else {
                listNode = listNode.levelInfo[0].forward;
            }
        }
        return result;
    }
    // endregion

    // region 通过排名查询

    /**
     * 查询指定排名区间的成员信息
     *
     * @param start 起始排名(0-based) inclusive
     * @param end   截止排名(0-based) inclusive
     * @return memberInfo
     */
    public List<Long2ObjectEntry<S>> zrangeByRank(int start, int end) {
        return zrangeByRankInternal(start, end, false);
    }

    /**
     * 查询指定逆序排名区间的成员信息
     *
     * @param start 起始排名(0-based) inclusive
     * @param end   截止排名(0-based) inclusive
     * @return memberInfo
     */
    public List<Long2ObjectEntry<S>> zrevrangeByRank(int start, int end) {
        return zrangeByRankInternal(start, end, true);
    }

    /**
     * 查询指定排名区间的成员id和分数，start和end都是从0开始的。
     *
     * @param start   起始排名(0-based) inclusive
     * @param end     截止排名(0-based) inclusive
     * @param reverse 是否逆序返回
     * @return memberInfo
     */
    private List<Long2ObjectEntry<S>> zrangeByRankInternal(int start, int end, boolean reverse) {
        final int zslLength = zsl.length();

        start = ZSetUtils.convertStartRank(start, zslLength);
        end = ZSetUtils.convertEndRank(end, zslLength);

        if (ZSetUtils.isRankRangeEmpty(start, end, zslLength)) {
            return new ArrayList<>();
        }

        int rangeLen = end - start + 1;

        SkipListNode<S> listNode;

        /* start >= 0，大于0表示需要进行调整 */
        /* Check if starting point is trivial, before doing log(N) lookup. */
        if (reverse) {
            listNode = start > 0 ? zsl.zslGetElementByRank(zslLength - start) : zsl.tail;
        } else {
            listNode = start > 0 ? zsl.zslGetElementByRank(start + 1) : zsl.header.levelInfo[0].forward;
        }

        final List<Long2ObjectEntry<S>> result = new ArrayList<>(rangeLen);
        while (rangeLen-- > 0 && listNode != null) {
            result.add(new ZSetEntry<>(listNode.obj, listNode.score));
            listNode = reverse ? listNode.backward : listNode.levelInfo[0].forward;
        }
        return result;
    }
    // endregion

    // region 统计分数人数

    /**
     * 返回有序集key中，score值在指定区间(包括score值等于start或end)的成员
     *
     * @param start 起始分数
     * @param end   截止分数
     * @return 分数区间段内的成员数量
     */
    public int zcount(S start, S end) {
        return zcountInternal(zsl.newRangeSpec(start, end));
    }

    /**
     * 返回有序集key中，score值在指定区间的成员
     *
     * @param rangeSpec score区间描述信息
     * @return 分数区间段内的成员数量
     */
    public int zcount(ScoreRangeSpec<S> rangeSpec) {
        return zcountInternal(zsl.newRangeSpec(rangeSpec));
    }

    /**
     * 返回有序集key中，score值在指定区间的成员
     *
     * @param range score区间描述信息
     * @return 分数区间段内的成员数量
     */
    private int zcountInternal(final ZScoreRangeSpec<S> range) {
        final SkipListNode<S> firstNodeInRange = zsl.zslFirstInRange(range);
        if (firstNodeInRange != null) {
            final int firstNodeRank = zsl.zslGetRank(firstNodeInRange.score, firstNodeInRange.obj);

            /* 如果firstNodeInRange不为null，那么lastNode也一定不为null(最坏的情况下firstNode就是lastNode) */
            final SkipListNode<S> lastNodeInRange = zsl.zslLastInRange(range);
            assert lastNodeInRange != null;
            final int lastNodeRank = zsl.zslGetRank(lastNodeInRange.score, lastNodeInRange.obj);

            return lastNodeRank - firstNodeRank + 1;
        }
        return 0;
    }

    /**
     * @return zset中的成员数量
     */
    public int zcard() {
        return zsl.length();
    }

    // endregion

    // region 迭代

    /**
     * 迭代有序集中的所有元素
     *
     * @return iterator
     */
    @Nonnull
    public Iterator<Long2ObjectEntry<S>> zscan() {
        return zscan(0);
    }

    /**
     * 从指定偏移量开始迭代有序集中的元素
     *
     * @param offset 偏移量，如果小于等于0，则等价于{@link #zscan()}
     * @return iterator
     */
    @Nonnull
    public Iterator<Long2ObjectEntry<S>> zscan(int offset) {
        if (offset <= 0) {
            return new ZSetItr(zsl.header.directForward());
        }

        if (offset >= zsl.length()) {
            return new ZSetItr(null);
        }

        return new ZSetItr(zsl.zslGetElementByRank(offset + 1));
    }

    @Nonnull
    @Override
    public Iterator<Long2ObjectEntry<S>> iterator() {
        return zscan(0);
    }

    /**
     * {@link Iterator#next()}总是返回相同的{@link Entry}对象，外部在迭代时不可以保持其引用。
     * 改迭代器是为了避免创建大量的{@link Entry}对象。
     *
     * @param offset 偏移量
     * @return 数组
     */
    @Nonnull
    public Iterator<Long2ObjectEntry<S>> fastzscan(int offset) {
        if (offset <= 0) {
            return new FastZSetItr(zsl.header.directForward());
        }

        if (offset >= zsl.length()) {
            return new FastZSetItr(null);
        }

        return new FastZSetItr(zsl.zslGetElementByRank(offset + 1));
    }

    /**
     * @return 总是返回相同的{@link Entry}对象，外部在迭代时不可以保持其引用。
     * 改迭代器是为了避免创建大量的{@link Entry}对象。
     */
    @Nonnull
    public Iterator<Long2ObjectEntry<S>> fastIterator() {
        return fastzscan(0);
    }
    // endregion

    /**
     * @return zset中当前的成员信息，用于debug
     */
    public String dump() {
        return zsl.dump();
    }
    // ------------------------------------------------------- 内部实现 ----------------------------------------

    /**
     * 跳表
     * 注意：跳表的排名是从1开始的。
     *
     * @author wjybxx
     * @version 1.0
     * date - 2019/11/4
     */
    private static class SkipList<S> {

        /**
         * 更新节点使用的缓存 - 避免频繁的申请空间
         */
        @SuppressWarnings("unchecked")
        private final SkipListNode<S>[] updateCache = new SkipListNode[ZSKIPLIST_MAXLEVEL];
        private final int[] rankCache = new int[ZSKIPLIST_MAXLEVEL];

        private final LongComparator objComparator;
        private final ScoreHandler<S> scoreHandler;

        /**
         * 修改次数 - 防止错误的迭代
         */
        private int modCount = 0;

        /**
         * 跳表头结点 - 哨兵
         * 1. 可以简化判定逻辑
         * 2. 恰好可以使得rank从1开始
         */
        private final SkipListNode<S> header;

        /**
         * 跳表尾节点
         */
        private SkipListNode<S> tail;

        /**
         * 跳表成员个数
         * 注意：head头指针不包含在length计数中。
         */
        private int length = 0;

        /**
         * level表示SkipList的总层数，即所有节点层数的最大值。
         */
        private int level = 1;

        SkipList(LongComparator objComparator, ScoreHandler<S> scoreHandler) {
            this.objComparator = objComparator;
            this.scoreHandler = scoreHandler;
            this.header = zslCreateNode(ZSKIPLIST_MAXLEVEL, null, 0);
        }

        /**
         * 插入一个新的节点到跳表。
         * 这里假定成员已经不存在（直到调用方执行该方法）。
         * <p>
         * zslInsert a new node in the skiplist. Assumes the element does not already
         * exist (up to the caller to enforce that).
         * <pre>
         *             header                    newNode
         *               _                                                 _
         * level - 1    |_| pre                                           |_|
         *  |           |_| pre                    _                      |_|
         *  |           |_| pre  _                |_|                     |_|
         *  |           |_|  ↓  |_| pre  _        |_|      _              |_|
         *  |           |_|     |_|  ↓  |_| pre   |_|     |_|             |_|
         *  |           |_|     |_|     |_| pre   |_|     |_|      _      |_|
         *  |           |_|     |_|     |_| pre   |_|     |_|     |_|     |_|
         *  0           |0|     |1|     |2| pre   |_|     |3|     |4|     |5|
         * </pre>
         *
         * @param score 分数
         * @param obj   obj 分数对应的成员id
         */
        @SuppressWarnings("UnusedReturnValue")
        SkipListNode zslInsert(S score, long obj) {
            // 新节点的level
            final int level = ZSetUtils.zslRandomLevel();

            // update - 需要更新后继节点的Node，新节点各层的前驱节点
            // 1. 分数小的节点
            // 2. 分数相同但id小的节点（分数相同时根据数据排序）
            // rank - 新节点各层前驱的当前排名
            // 这里不必创建一个ZSKIPLIST_MAXLEVEL长度的数组，它取决于插入节点后的新高度，你在别处看见的代码会造成大量的空间浪费，增加GC压力。
            // 如果创建的都是ZSKIPLIST_MAXLEVEL长度的数组，那么应该实现缓存

            final SkipListNode<S>[] update = updateCache;
            final int[] rank = rankCache;
            final int realLength = Math.max(level, this.level);
            try {
                // preNode - 新插入节点的前驱节点
                SkipListNode<S> preNode = header;
                for (int i = this.level - 1; i >= 0; i--) {
                    /* store rank that is crossed to reach the insert position */
                    if (i == (this.level - 1)) {
                        // 起始点，也就是head，它的排名就是0
                        rank[i] = 0;
                    } else {
                        // 由于是回溯降级继续遍历，因此其初始排名是前一次遍历的排名
                        rank[i] = rank[i + 1];
                    }

                    while (preNode.levelInfo[i].forward != null &&
                            compareScoreAndObj(preNode.levelInfo[i].forward, score, obj) < 0) {
                        // preNode的后继节点仍然小于要插入的节点，需要继续前进，同时累计排名
                        rank[i] += preNode.levelInfo[i].span;
                        preNode = preNode.levelInfo[i].forward;
                    }

                    // 这是要插入节点的第i层的前驱节点，此时触发降级
                    update[i] = preNode;
                }

                if (level > this.level) {
                    /* 新节点的层级大于当前层级，那么高出来的层级导致需要更新head，且排名和跨度是固定的 */
                    for (int i = this.level; i < level; i++) {
                        rank[i] = 0;
                        update[i] = this.header;
                        update[i].levelInfo[i].span = this.length;
                    }
                    this.level = level;
                }

                /* 由于我们允许的重复score，并且zslInsert(该方法)的调用者在插入前必须测试要插入的member是否已经在hash表中。
                 * 因此我们假设key（obj）尚未被插入，并且重复插入score的情况永远不会发生。*/
                /* we assume the key is not already inside, since we allow duplicated
                 * scores, and the re-insertion of score and redis object should never
                 * happen since the caller of zslInsert() should test in the hash table
                 * if the element is already inside or not.*/
                final SkipListNode<S> newNode = zslCreateNode(level, score, obj);

                /* 这些节点的高度小于等于新插入的节点的高度，需要更新指针。此外它们当前的跨度被拆分了两部分，需要重新计算。 */
                for (int i = 0; i < level; i++) {
                    /* 链接新插入的节点 */
                    newNode.levelInfo[i].forward = update[i].levelInfo[i].forward;
                    update[i].levelInfo[i].forward = newNode;

                    /* rank[0] 是新节点的直接前驱的排名，每一层都有一个前驱，可以通过彼此的排名计算跨度 */
                    /* 计算新插入节点的跨度 和 重新计算所有前驱节点的跨度，之前的跨度被拆分为了两份*/
                    /* update span covered by update[i] as newNode is inserted here */
                    newNode.levelInfo[i].span = update[i].levelInfo[i].span - (rank[0] - rank[i]);
                    update[i].levelInfo[i].span = (rank[0] - rank[i]) + 1;
                }

                /*  这些节点高于新插入的节点，它们的跨度可以简单的+1 */
                /* increment span for untouched levels */
                for (int i = level; i < this.level; i++) {
                    update[i].levelInfo[i].span++;
                }

                /* 设置新节点的前向节点(回溯节点) - 这里不包含header，一定注意 */
                newNode.backward = (update[0] == this.header) ? null : update[0];

                /* 设置新节点的后向节点 */
                if (newNode.levelInfo[0].forward != null) {
                    newNode.levelInfo[0].forward.backward = newNode;
                } else {
                    this.tail = newNode;
                }

                this.length++;
                this.modCount++;

                return newNode;
            } finally {
                ZSetUtils.releaseUpdate(update, realLength);
                ZSetUtils.releaseRank(rank, realLength);
            }
        }

        /**
         * Delete an element with matching score/object from the skiplist.
         *
         * @param score 分数用于快速定位节点
         * @param obj   用于确定节点是否是对应的数据节点
         */
        @SuppressWarnings("UnusedReturnValue")
        boolean zslDelete(S score, long obj) {
            // update - 需要更新后继节点的Node
            // 1. 分数小的节点
            // 2. 分数相同但id小的节点（分数相同时根据数据排序）
            final SkipListNode<S>[] update = updateCache;
            final int realLength = this.level;
            try {
                SkipListNode<S> preNode = this.header;
                for (int i = this.level - 1; i >= 0; i--) {
                    while (preNode.levelInfo[i].forward != null &&
                            compareScoreAndObj(preNode.levelInfo[i].forward, score, obj) < 0) {
                        // preNode的后继节点仍然小于要删除的节点，需要继续前进
                        preNode = preNode.levelInfo[i].forward;
                    }
                    // 这是目标节点第i层的可能前驱节点
                    update[i] = preNode;
                }

                /* 由于可能多个节点拥有相同的分数，因此必须同时比较score和object */
                /* We may have multiple elements with the same score, what we need
                 * is to find the element with both the right score and object. */
                final SkipListNode<S> targetNode = preNode.levelInfo[0].forward;
                if (targetNode != null && scoreEquals(targetNode.score, score) && objEquals(targetNode.obj, obj)) {
                    zslDeleteNode(targetNode, update);
                    return true;
                }

                /* not found */
                return false;
            } finally {
                ZSetUtils.releaseUpdate(update, realLength);
            }
        }

        /**
         * Internal function used by zslDelete, zslDeleteByScore and zslDeleteByRank
         *
         * @param deleteNode 要删除的节点
         * @param update     可能要更新的节点们
         */
        private void zslDeleteNode(final SkipListNode<S> deleteNode, final SkipListNode[] update) {
            for (int i = 0; i < this.level; i++) {
                if (update[i].levelInfo[i].forward == deleteNode) {
                    // 这些节点的高度小于等于要删除的节点，需要合并两个跨度
                    update[i].levelInfo[i].span += deleteNode.levelInfo[i].span - 1;
                    update[i].levelInfo[i].forward = deleteNode.levelInfo[i].forward;
                } else {
                    // 这些节点的高度高于要删除的节点，它们的跨度可以简单的 -1
                    update[i].levelInfo[i].span--;
                }
            }

            if (deleteNode.levelInfo[0].forward != null) {
                // 要删除的节点有后继节点
                deleteNode.levelInfo[0].forward.backward = deleteNode.backward;
            } else {
                // 要删除的节点是tail节点
                this.tail = deleteNode.backward;
            }

            // 如果删除的节点是最高等级的节点，则检查是否需要降级
            if (deleteNode.levelInfo.length == this.level) {
                while (this.level > 1 && this.header.levelInfo[this.level - 1].forward == null) {
                    // 如果最高层没有后继节点，则降级
                    this.level--;
                }
            }

            this.length--;
            this.modCount++;
        }

        /**
         * 判断zset中的数据所属的范围是否和指定range存在交集(intersection)。
         * 它不代表zset存在指定范围内的数据。
         * Returns if there is a part of the zset is in range.
         * <pre>
         *                         ZSet
         *              min ____________________ max
         *                 |____________________|
         *   min ______________ max  min _____________
         *      |______________|        |_____________|
         *          Range                   Range
         * </pre>
         *
         * @param range 范围描述信息
         * @return true/false
         */
        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        boolean zslIsInRange(ZScoreRangeSpec<S> range) {
            if (isScoreRangeEmpty(range)) {
                // 传进来的范围为空
                return false;
            }

            if (this.tail == null || !zslValueGteMin(this.tail.score, range)) {
                // 列表有序，按照从score小到大，如果尾部节点数据小于最小值，那么一定不在区间范围内
                return false;
            }

            final SkipListNode<S> firstNode = this.header.levelInfo[0].forward;
            if (firstNode == null || !zslValueLteMax(firstNode.score, range)) {
                // 列表有序，按照从score小到大，如果首部节点数据大于最大值，那么一定不在范围内
                return false;
            }
            return true;
        }

        /**
         * 测试score范围信息是否为空(无效)
         *
         * @param range 范围描述信息
         * @return true/false
         */
        private boolean isScoreRangeEmpty(ZScoreRangeSpec<S> range) {
            // 这里和redis有所区别，这里min一定小于等于max
            return scoreEquals(range.min, range.max) && (range.minex || range.maxex);
        }

        /**
         * 找出第一个在指定范围内的节点。如果没有符合的节点，则返回null。
         * <p>
         * Find the first node that is contained in the specified range.
         * Returns NULL when no element is contained in the range.
         *
         * @param range 范围描述符
         * @return 不存在返回null
         */
        @Nullable
        SkipListNode<S> zslFirstInRange(ZScoreRangeSpec<S> range) {
            /* zset数据范围与指定范围没有交集，可提前返回，减少不必要的遍历 */
            /* If everything is out of range, return early. */
            if (!zslIsInRange(range)) {
                return null;
            }

            SkipListNode<S> lastNodeLtMin = this.header;
            for (int i = this.level - 1; i >= 0; i--) {
                /* 前进直到出现后继节点大于等于指定最小值的节点 */
                /* Go forward while *OUT* of range. */
                while (lastNodeLtMin.levelInfo[i].forward != null &&
                        !zslValueGteMin(lastNodeLtMin.levelInfo[i].forward.score, range)) {
                    // 如果当前节点的后继节点仍然小于指定范围的最小值，则继续前进
                    lastNodeLtMin = lastNodeLtMin.levelInfo[i].forward;
                }
            }

            /* 这里的上下文表明了，一定存在一个节点的值大于等于指定范围的最小值，因此下一个节点一定不为null */
            /* This is an inner range, so the next node cannot be NULL. */
            final SkipListNode<S> firstNodeGteMin = lastNodeLtMin.levelInfo[0].forward;
            assert firstNodeGteMin != null;

            /* 如果该节点的数据大于max，则不存在再范围内的节点 */
            /* Check if score <= max. */
            if (!zslValueLteMax(firstNodeGteMin.score, range)) {
                return null;
            }
            return firstNodeGteMin;
        }

        /**
         * 找出最后一个在指定范围内的节点。如果没有符合的节点，则返回null。
         * <p>
         * Find the last node that is contained in the specified range.
         * Returns NULL when no element is contained in the range.
         *
         * @param range 范围描述信息
         * @return 不存在返回null
         */
        @Nullable
        SkipListNode<S> zslLastInRange(ZScoreRangeSpec<S> range) {
            /* zset数据范围与指定范围没有交集，可提前返回，减少不必要的遍历 */
            /* If everything is out of range, return early. */
            if (!zslIsInRange(range)) {
                return null;
            }

            SkipListNode<S> lastNodeLteMax = this.header;
            for (int i = this.level - 1; i >= 0; i--) {
                /* Go forward while *IN* range. */
                while (lastNodeLteMax.levelInfo[i].forward != null &&
                        zslValueLteMax(lastNodeLteMax.levelInfo[i].forward.score, range)) {
                    // 如果当前节点的后继节点仍然小于最大值，则继续前进
                    lastNodeLteMax = lastNodeLteMax.levelInfo[i].forward;
                }
            }

            /* 这里的上下文表明一定存在一个节点的值小于指定范围的最大值，因此当前节点一定不为null */
            /* This is an inner range, so this node cannot be NULL. */
            assert lastNodeLteMax != null;

            /* Check if score >= min. */
            if (!zslValueGteMin(lastNodeLteMax.score, range)) {
                return null;
            }
            return lastNodeLteMax;
        }

        /**
         * 删除指定分数区间的所有节点。
         * <b>Note</b>: 该方法引用了ZSet的哈希表视图，以便从哈希表中删除成员。
         * <p>
         * Delete all the elements with score between min and max from the skiplist.
         * Min and max are inclusive, so a score >= min || score <= max is deleted.
         * Note that this function takes the reference to the hash table view of the
         * sorted set, in order to remove the elements from the hash table too.
         *
         * @param range 范围描述符
         * @param dict  对象id到score的映射
         * @return 删除的节点数量
         */
        int zslDeleteRangeByScore(ZScoreRangeSpec<S> range, Long2ObjectMap<S> dict) {
            final SkipListNode<S>[] update = updateCache;
            final int realLength = this.level;
            try {
                int removed = 0;

                SkipListNode<S> lastNodeLtMin = this.header;
                for (int i = this.level - 1; i >= 0; i--) {
                    while (lastNodeLtMin.levelInfo[i].forward != null &&
                            !zslValueGteMin(lastNodeLtMin.levelInfo[i].forward.score, range)) {
                        lastNodeLtMin = lastNodeLtMin.levelInfo[i].forward;
                    }
                    update[i] = lastNodeLtMin;
                }

                /* 当前节点是小于目标范围最小值的最后一个节点，它的下一个节点可能为null，或大于等于最小值 */
                /* Current node is the last with score < or <= min. */
                SkipListNode<S> firstNodeGteMin = lastNodeLtMin.levelInfo[0].forward;

                /* 删除在范围内的节点(小于等于最大值的节点) */
                /* Delete nodes while in range. */
                while (firstNodeGteMin != null
                        && zslValueLteMax(firstNodeGteMin.score, range)) {
                    final SkipListNode<S> next = firstNodeGteMin.levelInfo[0].forward;
                    zslDeleteNode(firstNodeGteMin, update);
                    dict.remove(firstNodeGteMin.obj);
                    removed++;
                    firstNodeGteMin = next;
                }
                return removed;
            } finally {
                ZSetUtils.releaseUpdate(update, realLength);
            }
        }

        /**
         * 删除指定排名区间的所有成员。包括start和end。
         * <b>Note</b>: start和end基于从1开始
         * <p>
         * Delete all the elements with rank between start and end from the skiplist.
         * Start and end are inclusive. Note that start and end need to be 1-based
         *
         * @param start 起始排名 inclusive
         * @param end   截止排名 inclusive
         * @param dict  member -> score的字典
         * @return 删除的成员数量
         */
        int zslDeleteRangeByRank(int start, int end, Long2ObjectMap<S> dict) {
            final SkipListNode<S>[] update = updateCache;
            final int realLength = this.level;
            try {
                /* 已遍历的真实成员数量，表示成员的真实排名 */
                int traversed = 0;
                int removed = 0;

                SkipListNode<S> lastNodeLtStart = this.header;
                for (int i = this.level - 1; i >= 0; i--) {
                    while (lastNodeLtStart.levelInfo[i].forward != null &&
                            (traversed + lastNodeLtStart.levelInfo[i].span) < start) {
                        // 下一个节点的排名还未到范围内，继续前进
                        traversed += lastNodeLtStart.levelInfo[i].span;
                        lastNodeLtStart = lastNodeLtStart.levelInfo[i].forward;
                    }
                    update[i] = lastNodeLtStart;
                }

                traversed++;

                /* levelInfo[0] 最下面一层就是要删除节点的直接前驱 */
                SkipListNode<S> firstNodeGteStart = lastNodeLtStart.levelInfo[0].forward;
                while (firstNodeGteStart != null && traversed <= end) {
                    final SkipListNode<S> next = firstNodeGteStart.levelInfo[0].forward;
                    zslDeleteNode(firstNodeGteStart, update);
                    dict.remove(firstNodeGteStart.obj);
                    removed++;
                    traversed++;
                    firstNodeGteStart = next;
                }
                return removed;
            } finally {
                ZSetUtils.releaseUpdate(update, realLength);
            }
        }

        /**
         * 删除指定排名的成员 - 批量删除比单个删除更快捷
         * (该方法非原生方法)
         *
         * @param rank 排名 1-based
         * @param dict member -> score的字典
         * @return 删除的节点
         */
        SkipListNode<S> zslDeleteByRank(int rank, Long2ObjectMap<S> dict) {
            final SkipListNode<S>[] update = updateCache;
            final int realLength = this.level;
            try {
                int traversed = 0;
                SkipListNode<S> lastNodeLtStart = this.header;
                for (int i = this.level - 1; i >= 0; i--) {
                    while (lastNodeLtStart.levelInfo[i].forward != null &&
                            (traversed + lastNodeLtStart.levelInfo[i].span) < rank) {
                        // 下一个节点的排名还未到范围内，继续前进
                        traversed += lastNodeLtStart.levelInfo[i].span;
                        lastNodeLtStart = lastNodeLtStart.levelInfo[i].forward;
                    }
                    update[i] = lastNodeLtStart;
                }

                /* levelInfo[0] 最下面一层就是要删除节点的直接前驱 */
                final SkipListNode<S> targetRankNode = lastNodeLtStart.levelInfo[0].forward;
                if (null != targetRankNode) {
                    zslDeleteNode(targetRankNode, update);
                    dict.remove(targetRankNode.obj);
                    return targetRankNode;
                } else {
                    return null;
                }
            } finally {
                ZSetUtils.releaseUpdate(update, realLength);
            }
        }

        /**
         * 通过score和key查找成员所属的排名。
         * 如果找不到对应的成员，则返回0。
         * <b>Note</b>：排名从1开始
         * <p>
         * Find the rank for an element by both score and key.
         * Returns 0 when the element cannot be found, rank otherwise.
         * Note that the rank is 1-based due to the span of zsl->header to the
         * first element.
         *
         * @param score 节点分数
         * @param obj   节点对应的数据id
         * @return 排名，从1开始
         */
        int zslGetRank(S score, long obj) {
            int rank = 0;
            SkipListNode<S> firstNodeGteScore = this.header;
            for (int i = this.level - 1; i >= 0; i--) {
                while (firstNodeGteScore.levelInfo[i].forward != null &&
                        compareScoreAndObj(firstNodeGteScore.levelInfo[i].forward, score, obj) <= 0) {
                    // <= 也继续前进，也就是我们期望在目标节点停下来，这样rank也不必特殊处理
                    rank += firstNodeGteScore.levelInfo[i].span;
                    firstNodeGteScore = firstNodeGteScore.levelInfo[i].forward;
                }

                /* firstNodeGteScore might be equal to zsl->header, so test if firstNodeGteScore is header */
                if (firstNodeGteScore != this.header && objEquals(firstNodeGteScore.obj, obj)) {
                    // 可能在任意层找到
                    return rank;
                }
            }
            return 0;
        }

        /**
         * 查找指定排名的成员数据，如果不存在，则返回Null。
         * 注意：排名从1开始
         * <p>
         * Finds an element by its rank. The rank argument needs to be 1-based.
         *
         * @param rank 排名，1开始
         * @return element
         */
        @Nullable
        SkipListNode<S> zslGetElementByRank(int rank) {
            int traversed = 0;
            SkipListNode<S> firstNodeGteRank = this.header;
            for (int i = this.level - 1; i >= 0; i--) {
                while (firstNodeGteRank.levelInfo[i].forward != null &&
                        (traversed + firstNodeGteRank.levelInfo[i].span) <= rank) {
                    // <= rank 表示我们期望在目标节点停下来
                    traversed += firstNodeGteRank.levelInfo[i].span;
                    firstNodeGteRank = firstNodeGteRank.levelInfo[i].forward;
                }

                if (traversed == rank) {
                    // 可能在任意层找到该排名的数据
                    return firstNodeGteRank;
                }
            }
            return null;
        }

        /**
         * @return 跳表中的成员数量
         */
        private int length() {
            return length;
        }

        /**
         * 创建一个skipList的节点
         *
         * @param level 节点的高度
         * @param score 成员分数
         * @param obj   成员id
         * @return node
         */
        private static <S> SkipListNode<S> zslCreateNode(int level, S score, long obj) {
            final SkipListNode<S> node = new SkipListNode<>(obj, score, new SkipListLevel[level]);
            for (int index = 0; index < level; index++) {
                node.levelInfo[index] = new SkipListLevel<>();
            }
            return node;
        }

        /**
         * 计算两个score的和
         */
        private S sum(S score1, S score2) {
            return scoreHandler.sum(score1, score2);
        }

        /**
         * @param start 起始分数
         * @param end   截止分数
         * @return spec
         */
        private ZScoreRangeSpec<S> newRangeSpec(S start, S end) {
            return newRangeSpec(start, false, end, false);
        }

        /**
         * @param rangeSpec 开放给用户的范围描述信息
         * @return spec
         */
        private ZScoreRangeSpec<S> newRangeSpec(ScoreRangeSpec<S> rangeSpec) {
            return newRangeSpec(rangeSpec.getStart(), rangeSpec.isStartEx(), rangeSpec.getEnd(), rangeSpec.isEndEx());
        }

        /**
         * @param start   起始分数
         * @param startEx 是否去除起始分数
         * @param end     截止分数
         * @param endEx   是否去除截止分数
         * @return spec
         */
        private ZScoreRangeSpec<S> newRangeSpec(S start, boolean startEx, S end, boolean endEx) {
            if (compareScore(start, end) <= 0) {
                return new ZScoreRangeSpec<>(start, startEx, end, endEx);
            } else {
                return new ZScoreRangeSpec<>(end, endEx, start, startEx);
            }
        }

        /**
         * 值是否大于等于下限
         *
         * @param value 要比较的score
         * @param spec  范围描述信息
         * @return true/false
         */
        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        boolean zslValueGteMin(S value, ZScoreRangeSpec<S> spec) {
            return spec.minex ? compareScore(value, spec.min) > 0 : compareScore(value, spec.min) >= 0;
        }

        /**
         * 值是否小于等于上限
         *
         * @param value 要比较的score
         * @param spec  范围描述信息
         * @return true/false
         */
        boolean zslValueLteMax(S value, ZScoreRangeSpec<S> spec) {
            return spec.maxex ? compareScore(value, spec.max) < 0 : compareScore(value, spec.max) <= 0;
        }


        /**
         * 比较score和key的大小，分数作为第一排序条件，然后，相同分数的成员按照字典规则相对排序
         *
         * @param forward 后继节点
         * @param score   分数
         * @param obj     成员的键
         * @return 0 表示equals
         */
        private int compareScoreAndObj(SkipListNode<S> forward, S score, long obj) {
            final int scoreCompareR = compareScore(forward.score, score);
            if (scoreCompareR != 0) {
                return scoreCompareR;
            }
            return compareObj(forward.obj, obj);
        }

        /**
         * 比较两个成员的key，<b>必须保证当且仅当两个键相等的时候返回0</b>
         *
         * @return 0表示相等
         */
        private int compareObj(long objA, long objB) {
            return objComparator.compare(objA, objB);
        }

        /**
         * 判断两个对象是否相等
         *
         * @return true/false
         * @apiNote 使用compare == 0判断相等
         */
        private boolean objEquals(long objA, long objB) {
            // 不使用equals，而是使用compare
            return compareObj(objA, objB) == 0;
        }

        /**
         * 比较两个分数的大小
         *
         * @return 0表示相等
         */
        private int compareScore(S score1, S score2) {
            return scoreHandler.compare(score1, score2);
        }

        /**
         * 判断第一个分数是否和第二个分数相等
         *
         * @return true/false
         * @apiNote 使用compare == 0判断相等
         */
        private boolean scoreEquals(S score1, S score2) {
            return compareScore(score1, score2) == 0;
        }

        /**
         * 获取跳表的堆内存视图
         *
         * @return string
         */
        String dump() {
            final StringBuilder sb = new StringBuilder("{level = 0, nodeArray:[\n");
            SkipListNode<S> curNode = this.header.directForward();
            int rank = 0;
            while (curNode != null) {
                sb.append("{rank:").append(rank++)
                        .append(",obj:").append(curNode.obj)
                        .append(",score:").append(curNode.score);

                curNode = curNode.directForward();

                if (curNode != null) {
                    sb.append("},\n");
                } else {
                    sb.append("}\n");
                }
            }
            return sb.append("]}").toString();
        }
    }

    /**
     * 跳表节点
     */
    private static class SkipListNode<S> implements Long2ObjectEntry<S> {
        /**
         * 节点对应的数据id
         */
        final long obj;
        /**
         * 该节点数据对应的评分 - 如果要通用的话，这里将来将是一个泛型对象，需要实现{@link Comparable}。
         */
        final S score;
        /**
         * 该节点的层级信息
         * level[]存放指向各层链表后一个节点的指针（后向指针）。
         */
        final SkipListLevel<S>[] levelInfo;
        /**
         * 该节点的前向指针
         * <b>NOTE:</b>(不包含header)
         * backward字段是指向链表前一个节点的指针（前向指针）。
         * 节点只有1个前向指针，所以只有第1层链表是一个双向链表。
         */
        SkipListNode<S> backward;

        private SkipListNode(long obj, S score, SkipListLevel[] levelInfo) {
            this.obj = obj;
            this.score = score;
            // noinspection unchecked
            this.levelInfo = levelInfo;
        }

        /**
         * @return 该节点的直接后继节点
         */
        SkipListNode<S> directForward() {
            return levelInfo[0].forward;
        }

        @Override
        public long getLongMember() {
            return obj;
        }

        @Override
        public S getScore() {
            return score;
        }
    }

    /**
     * 跳表层级
     */
    private static class SkipListLevel<S> {
        /**
         * 每层对应1个后向指针 (后继节点)
         */
        SkipListNode<S> forward;
        /**
         * 到后继节点之间的跨度
         * 它表示当前的指针跨越了多少个节点。span用于计算成员排名(rank)，这是Redis对于SkipList做的一个扩展。
         */
        int span;
    }

    // region 迭代

    /**
     * ZSet迭代器
     * Q: 为什么不写在{@link SkipList}中？
     * A: 因为删除数据需要访问{@link #dict}。
     */
    private class ZSetItr implements Iterator<Long2ObjectEntry<S>> {

        private SkipListNode<S> lastReturned;
        private SkipListNode<S> next;
        int expectedModCount = zsl.modCount;

        ZSetItr(SkipListNode<S> next) {
            this.next = next;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Long2ObjectEntry<S> next() {
            checkForComodification();

            if (next == null) {
                throw new NoSuchElementException();
            }

            lastReturned = next;
            next = next.directForward();

            return nextMember(lastReturned);
        }

        protected Long2ObjectEntry<S> nextMember(SkipListNode<S> lastReturned) {
            return new ZSetEntry<>(lastReturned.obj, lastReturned.score);
        }

        @Override
        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }

            checkForComodification();

            // remove lastReturned
            dict.remove(lastReturned.obj);
            zsl.zslDelete(lastReturned.score, lastReturned.obj);

            // reset lastReturned
            lastReturned = null;
            expectedModCount = zsl.modCount;
        }

        final void checkForComodification() {
            if (zsl.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    private class FastZSetItr extends ZSetItr {

        FastZSetItr(SkipListNode<S> next) {
            super(next);
        }

        @Override
        protected Long2ObjectEntry<S> nextMember(SkipListNode<S> lastReturned) {
            return lastReturned;
        }

    }

    public static class ZSetEntry<S> implements Long2ObjectEntry<S> {

        private final long member;
        private final S score;

        ZSetEntry(long member, S score) {
            this.member = member;
            this.score = score;
        }

        @Override
        public long getLongMember() {
            return member;
        }

        @Override
        public S getScore() {
            return score;
        }

        @Override
        public String toString() {
            return "{" +
                    "member=" + member +
                    ", score=" + score +
                    '}';
        }

    }

    // endregion
}
