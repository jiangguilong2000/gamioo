/*
 * Copyright 2015-2020 gamioo Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gamioo.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.ToIntFunction;

/**
 * 随机数相关操作工具类.
 * <p>
 * 本工具类中统一以{@link ThreadLocalRandom}为基础的封装
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class RandomUtils {

    private RandomUtils() {
    }

    /**
     * 返回一个随机Boolean值.
     *
     * @return 随机Boolean值
     */
    public static boolean nextBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    /**
     * 返回一个0到指定区间的随机数字.
     * <p>
     * 0 &lt;= random &lt; bound
     *
     * @param bound 最大值（不包含）
     * @return 返回一个0到指定区间的随机数字
     */
    public static int nextInt(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    /**
     * 返回一个指定区间的随机数字.
     * <p>
     * origin &lt;= random &lt; bound
     *
     * @param origin 最小值（包含）
     * @param bound  最大值（不包含）
     * @return 返回一个指定区间的随机数字
     */
    public static int nextInt(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }

    /**
     * 返回一个0到指定区间的随机数字.
     * <p>
     * 0 &lt;= random &lt; bound
     *
     * @param bound 最大值（不包含）
     * @return 返回一个0到指定区间的随机数字
     */
    public static long nextLong(long bound) {
        return ThreadLocalRandom.current().nextLong(bound);
    }

    /**
     * 返回一个指定区间的随机数字.
     * <p>
     * origin &lt;= random &lt; bound
     *
     * @param origin 最小值（包含）
     * @param bound  最大值（不包含）
     * @return 返回一个指定区间的随机数字
     */
    public static long nextLong(long origin, long bound) {
        return ThreadLocalRandom.current().nextLong(origin, bound);
    }

    /**
     * 返回一个随机Double值.
     *
     * @return 随机Double值
     */
    public static double nextDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    /**
     * 判定一次随机事件是否成功.
     *
     * <pre>
     * 如果rate&gt;=1,则百分百返回true.<br>
     * 如果rate&lt;=0,则百分百返回false.
     * </pre>
     *
     * @param rate 成功率
     * @return 如果成功返回true, 否则返回false.
     */
    public static boolean isSuccess(float rate) {
        return ThreadLocalRandom.current().nextFloat() < rate;
    }

    /**
     * 判定一次随机事件是否成功.
     *
     * <pre>
     * 如果rate&gt;=1,则百分百返回true.<br>
     * 如果rate&lt;=0,则百分百返回false.
     * </pre>
     *
     * @param rate 成功率
     * @return 如果成功返回true, 否则返回false.
     */
    public static boolean isSuccess(double rate) {
        return ThreadLocalRandom.current().nextDouble() < rate;
    }

    /**
     * 判定一次百分比的随机事件是否成功.
     * <p>
     * 参数自动转化为百分比单位，就是除100
     *
     * <pre>
     * RandomUtils.isSuccessByPercentage(rate) = RandomUtils.isSuccess(rate / 100D)
     * </pre>
     *
     * @param rate 成功率/100D
     * @return 如果成功返回true, 否则返回false.
     */
    public static boolean isSuccessByPercentage(long rate) {
        return isSuccess(rate / MathUtils.HUNDRED);
    }

    /**
     * 判定一次千分比的随机事件是否成功.
     * <p>
     * 参数自动转化为千分比单位，就是除1000
     *
     * <pre>
     * RandomUtils.isSuccessByPermillage(rate) = RandomUtils.isSuccess(rate / 1000D)
     * </pre>
     *
     * @param rate 成功率/1000D
     * @return 如果成功返回true, 否则返回false.
     */
    public static boolean isSuccessByPermillage(long rate) {
        return isSuccess(rate / MathUtils.THOUSAND);
    }

    /**
     * 在指定集合中随机出一个元素.
     * <p>
     * 所以元素无权重的随机.
     *
     * @param <T>  要随机集合里的元素类型
     * @param list 指定集合
     * @return 随机返回集合中的一个元素.
     */
    public static <T> T randomList(List<T> list) {
        // 没有东东的集合，随机个毛线啊...
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(nextInt(list.size()));
    }

    /**
     * 从一个List集合中随机出指定数量的元素.
     * <p>
     * <code>
     * source = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]<br>
     * random(source, 5) = [5, 3, 6, 7, 2]
     * </code>
     *
     * @param <T>    要随机集合里的元素类型
     * @param source List集合
     * @param num    指定数量
     * @return 如果源为空或指定数量小于1，则返回空集合，否则随机抽取元素组装新集合并返回
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> randomList(final List<T> source, int num) {
        // 没有源或要取的数小于1个就直接返回空列表
        if (source == null || num < 1) {
            return Collections.emptyList();
        }

        // 数量刚刚好
        if (source.size() <= num) {
            List<T> result = new ArrayList<>(source);
            Collections.shuffle(source);
            return result;
        }

        // 随机位，最后一个元素向前移动的方式
        Object[] rs = source.toArray();
        List<T> result = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            int index = nextInt(rs.length - i);
            result.add((T) rs[index]);
            rs[index] = rs[rs.length - 1 - i];
        }
        return result;
    }

    /**
     * 在指定集合中按权重随机出一个元素.
     * <p>
     * K为元素，如果是自定义对象记得重写HashCode和equals.<br>
     * V为权重，机率为V/(sum(All))
     *
     * @param <K>  要随机的元素类型，也是Map的Key
     * @param data 随机集合
     * @return 按权重随机返回集合中的一个元素.
     */
    public static <K> K randomByWeight(Map<K, Integer> data) {
        final int sum = data.values().stream().reduce(0, (a, b) -> a + b);
        if (sum <= 0) {
            return randomList(new ArrayList<>(data.keySet()));
        }

        final int random = nextInt(sum);
        int step = 0;
        for (Map.Entry<K, Integer> e : data.entrySet()) {
            step += e.getValue().intValue();
            if (step > random) {
                return e.getKey();
            }
        }
        throw new RuntimeException("randomByWeight的实现有Bug：" + random);
    }

    /**
     * 在指定集合中按权重随机出一个元素.
     * <p>
     * 权重，机率为V/(sum(All))
     *
     * @param <T>            要随机的元素类型
     * @param data           随机集合
     * @param weightFunction 元素中权重方法
     * @return 按权重随机返回集合中的一个元素
     */
    public static <T> T randomByWeight(List<T> data, ToIntFunction<? super T> weightFunction) {
        final int sum = data.stream().mapToInt(weightFunction).reduce(0, (a, b) -> a + b);
        if (sum <= 0) {
            return randomList(data);
        }

        final int random = nextInt(sum);
        int step = 0;
        for (T e : data) {
            step += weightFunction.applyAsInt(e);
            if (step > random) {
                return e;
            }
        }
        throw new RuntimeException("randomByWeight的实现有Bug：" + random);
    }

    /**
     * 在指定集合中按权重随机出指定数量个元素.
     * <p>
     * 权重，机率为V/(sum(All))
     *
     * @param <T>            要随机的元素类型
     * @param data           随机集合
     * @param weightFunction 元素中权重方法
     * @param num            指定数量
     * @return 按权重随机返回集合中的指定数量个元素
     */
    public static <T> List<T> randomByWeight(List<T> data, ToIntFunction<? super T> weightFunction, int num) {
        if (num <= 0) {
            return Collections.emptyList();
        }

        final int sum = data.stream().mapToInt(weightFunction).reduce(0, (a, b) -> a + b);
        if (sum <= 0) {
            return randomList(data, num);
        }

        List<T> result = new ArrayList<>(num);
        for (int i = 1; i <= num; i++) {
            final int random = nextInt(sum);
            int step = 0;
            for (T e : data) {
                step += weightFunction.applyAsInt(e);
                if (step > random) {
                    result.add(e);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 区间随机
     * <p>
     * max - min
     *
     * @param min 起始值
     * @param max 结束值
     * @return 随机数
     */
    public static int randomBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(max - min + 1) + min;
    }


    /**
     * 以 numerator/denominator的概率随机触发
     *
     * @param numerator   分子
     * @param denominator 分母
     * @return 是否触发
     * @throws IllegalArgumentException 分母为0时抛出异常，无法判断此时期望的return
     */
    public static boolean trigger(int numerator, int denominator) {
        if (denominator <= 0) {
            throw new IllegalArgumentException("denominator=" + denominator);
        }
        if (numerator <= 0) {
            return false;
        }
        if (numerator > denominator) {
            return true;
        }
        return nextInt(0, denominator) < numerator;
    }
}