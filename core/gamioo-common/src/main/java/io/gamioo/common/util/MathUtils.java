/*
 * Copyright 2015-2020 Gamioo Authors.
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

import io.gamioo.common.lang.Point;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 数学计算相关的工具类库.
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class MathUtils {
    /**
     * 一
     */
    public static final double ONE = 1.0;
    /**
     * 百
     */
    public static final double HUNDRED = 100.0;
    /**
     * 千
     */
    public static final double THOUSAND = 1000.0;
    /**
     * 万
     */
    public static final double TEN_THOUSAND = 1_0000.0;
    /**
     * 百万
     */
    public static final double MILLION = 100_0000.0;

    /**
     * 计算两个参数的和，如果相加出现溢出那就返回{@code int}的最大值.
     * <p>
     * 区别于JDK的方法，仅仅认同判定方案，游戏世界，溢出时那就修正一个合理的值，一般调用此方法的游戏逻辑决不能因异常而中断
     *
     * @param x 第一个参数
     * @param y 第二个参数
     * @return 两个参数的和
     * @see Math#addExact(int, int)
     */
    public static int addExact(int x, int y) {
        try {
            return Math.addExact(x, y);
        } catch (ArithmeticException e) {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * 计算两个参数的和，如果相加出现溢出那就返回{@code long}的最大值.
     * <p>
     * 区别于JDK的方法，仅仅认同判定方案，游戏世界，溢出时那就修正一个合理的值，一般调用此方法的游戏逻辑决不能因异常而中断
     *
     * @param x 第一个参数
     * @param y 第二个参数
     * @return 两个参数的和
     * @see Math#addExact(long, long)
     */
    public static long addExact(long x, long y) {
        try {
            return Math.addExact(x, y);
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
        }
    }

    /**
     * 计算两个参数的乘积，如果相乘出现溢出那就返回{@code int}的最大值.
     * <p>
     * 区别于JDK的方法，仅仅认同判定方案，游戏世界，溢出时那就修正一个合理的值，一般调用此方法的游戏逻辑决不能因异常而中断
     *
     * @param x 第一个参数
     * @param y 第二个参数
     * @return 两个参数的乘积
     * @see Math#multiplyExact(int, int)
     */
    public static int multiplyExact(int x, int y) {
        try {
            return Math.multiplyExact(x, y);
        } catch (ArithmeticException e) {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * 计算两个参数的乘积，如果相乘出现溢出那就返回{@code long}的最大值.
     * <p>
     * 区别于JDK的方法，仅仅认同判定方案，游戏世界，溢出时那就修正一个合理的值，一般调用此方法的游戏逻辑决不能因异常而中断
     *
     * @param x 第一个参数
     * @param y 第二个参数
     * @return 两个参数的乘积
     * @see Math#multiplyExact(long, long)
     */
    public static long multiplyExact(long x, long y) {
        try {
            return Math.multiplyExact(x, y);
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
        }
    }

    /**
     * 计算两点(x1,y1)到(x2,y2)的距离.
     * <p>
     * Math.sqrt(|x1-x2|² + |y1-y2|²)
     *
     * @param x1 坐标X1
     * @param y1 坐标Y1
     * @param x2 坐标X2
     * @param y2 坐标Y2
     * @return 两点的距离
     */
    public static double distance(int x1, int y1, int x2, int y2) {
        final double x = Math.abs(x1 - x2);
        final double y = Math.abs(y1 - y2);
        return Math.sqrt(x * x + y * y);
    }

    /**
     * 计算两点(x1,y1)到(x2,y2)的距离.
     * <p>
     * Math.sqrt(|x1-x2|² + |y1-y2|²)
     *
     * @param x1 坐标X1
     * @param y1 坐标Y1
     * @param x2 坐标X2
     * @param y2 坐标Y2
     * @return 两点的距离
     */
    public static double distance(double x1, double y1, double x2, double y2) {
        final double x = Math.abs(x1 - x2);
        final double y = Math.abs(y1 - y2);
        return Math.sqrt(x * x + y * y);
    }

    /**
     * 计算两点P1(x1,y1)到P2(x2,y2)的距离.
     * <p>
     * Math.sqrt(|x1-x2|² + |y1-y2|²)
     *
     * @param p1 坐标1
     * @param p2 坐标2
     * @return 两点的距离
     */
    public static double distance(Point p1, Point p2) {
        return distance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    /**
     * 判定两点(x1,y1)和(x2,y2)是否相邻.
     * <p>
     * 可用于两个AOI是否相邻判定
     *
     * @param x1 坐标X1
     * @param y1 坐标Y1
     * @param x2 坐标X2
     * @param y2 坐标Y2
     * @return 如果两坐标相邻返回true, 否则返回false
     */
    public static boolean adjacent(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) <= 1 && Math.abs(y1 - y2) <= 1;
    }

    /**
     * 判定两点P1(x1,y1)和P2(x2,y2)是否相邻.
     * <p>
     * 可用于两个AOI是否相邻判定
     *
     * @param p1 坐标1
     * @param p2 坐标2
     * @return 如果两坐标相邻返回true, 否则返回false
     */
    public static boolean adjacent(Point p1, Point p2) {
        return adjacent(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    /**
     * 向下取整，并返回int值.
     *
     * @param a 一个带有小数的数值
     * @return 返回向下取整后的int值
     */
    public static int floorInt(double a) {
        return (int) Math.floor(a);
    }

    /**
     * 向下取整，并返回long值.
     *
     * @param a 一个带有小数的数值
     * @return 返回向下取整后的long值
     */
    public static long floorLong(double a) {
        return (long) Math.floor(a);
    }

    /**
     * 向上取整，并返回int值.
     *
     * @param a 一个带有小数的数值
     * @return 返回向上取整后的int值
     */
    public static int ceilInt(double a) {
        return (int) Math.ceil(a);
    }

    /**
     * 向上取整，并返回long值.
     *
     * @param a 一个带有小数的数值
     * @return 返回向上取整后的long值
     */
    public static long ceilLong(double a) {
        return (long) Math.ceil(a);
    }

    /**
     * 4舍5入取整，并返回int值.
     *
     * @param a 一个带有小数的数值
     * @return 返回向上取整后的int值
     */
    public static int roundInt(double a) {
        return (int) Math.round(a);
    }

    /**
     * 4舍5入取整，并返回long值.
     *
     * @param a 一个带有小数的数值
     * @return 返回向上取整后的long值
     */
    public static long roundLong(double a) {
        return (long) Math.round(a);
    }

    /**
     * 格式化小数位数的方法.
     * <p>
     * 采用了{@link BigDecimal#setScale(int, RoundingMode)}方式来保留小数位数<br>
     * 默认舍入方式为4舍5入, 参考{@link RoundingMode#HALF_UP}
     *
     * @param value    原始值
     * @param newScale 保留小数位数
     * @return 返回要被保留指定小数位数的值.
     */
    public static float formatScale(float value, int newScale) {
        return formatScale(value, newScale, RoundingMode.HALF_UP);
    }

    /**
     * 格式化小数位数的方法.
     * <p>
     * 采用了{@link BigDecimal#setScale(int, RoundingMode)}方式来保留小数位数
     *
     * @param value    原始值
     * @param newScale 保留小数位数
     * @param mode     被保留位数后舍入方式，参考{@link RoundingMode}
     * @return 返回要被保留指定小数位数的值.
     */
    public static float formatScale(float value, int newScale, RoundingMode mode) {
        return BigDecimal.valueOf(value).setScale(newScale, mode).floatValue();
    }

    /**
     * 格式化小数位数的方法.
     * <p>
     * 采用了{@link BigDecimal#setScale(int, RoundingMode)}方式来保留小数位数<br>
     * 默认舍入方式为4舍5入, 参考{@link RoundingMode#HALF_UP}
     *
     * @param value    原始值
     * @param newScale 保留小数位数
     * @return 返回要被保留指定小数位数的值.
     */
    public static double formatScale(double value, int newScale) {
        return formatScale(value, newScale, RoundingMode.HALF_UP);
    }

    /**
     * 格式化小数位数的方法.
     * <p>
     * 采用了{@link BigDecimal#setScale(int, RoundingMode)}方式来保留小数位数
     *
     * @param value    原始值
     * @param newScale 保留小数位数
     * @param mode     被保留位数后舍入方式，参考{@link RoundingMode}
     * @return 返回要被保留指定小数位数的值.
     */
    public static double formatScale(double value, int newScale, RoundingMode mode) {
        return BigDecimal.valueOf(value).setScale(newScale, mode).doubleValue();
    }

    /**
     * N种资源掠夺最优计算方案.
     * <p>
     * 有N种资源，尝试抢其他的部分，但各种资源有一定的比例... <br>
     * 使用场景：SLG的城池掠夺资源计算
     *
     * @param <T>       资源类型
     * @param resources N种资源(参数选用LinkedHashMap，就是想按顺序优先扣前面的...)
     * @param max       掠夺的最大值
     * @param ratio     掠夺比例<b>建议：比例总和在100以内</b>
     * @return 一种最优的掠夺结果
     */
    public static <T> Map<T, Long> plunder(Map<T, Long> resources, long max, Map<T, Integer> ratio) {
        final Map<T, Long> result = new HashMap<>(resources.size());
        final int sum = ratio.values().stream().reduce(0, (a, b) -> a + b);
        final long step = max >= sum ? max / Math.min(1000, Math.max(10, ratio.values().stream().reduce(0, (a, b) -> a + b))) : max;
        // 总计要抢的资源量
        long total = max;

        while (total > 0) {
            // 标识是否还有资源可以抢...
            boolean flag = false;
            for (Map.Entry<T, Long> e : resources.entrySet()) {
                if (e.getValue() <= 0) {
                    continue;
                }

                // 只要有一种资源大于0都算还有资源
                flag = true;

                // 比例+小步长随便，让抢出来的资源效果更好些...
                long selfStep = step * ratio.getOrDefault(e.getKey(), 1) + RandomUtils.nextLong(step);
                long temp = selfStep > total ? total : selfStep;

                // 如果资源不足，就以当前有的抢光就好了...
                if (e.getValue() < temp) {
                    temp = e.getValue();
                }

                // 最终本次抢的值
                final long value = temp;
                e.setValue(e.getValue() - value);
                total -= temp;
                result.compute(e.getKey(), (k, v) -> v == null ? value : v.longValue() + value);

                // 抢满了就退出啦...
                if (total <= 0) {
                    break;
                }
            }

            // 没有资源可以抢时，就直接退出了...
            if (!flag) {
                break;
            }
        }
        return result;
    }

    /**
     * long类型的数值按比率转化为double类型的值.
     * <p>
     * 由于配置表在转化中精度丢失问题，建议策划配置的是long类型的数值，所以就有了这个转化方法。 <br>
     * 比如：约定XX列为百分比，那配置50，就是50%，等于0.5
     *
     * @param value long类型的数值
     * @param ratio 比率
     * @return double类型的值
     */
    public static double longToDouble(long value, double ratio) {
        return value / ratio;
    }

    /**
     * long类型的数值以千分比转化为double类型的值.
     * <p>
     * 参考 {@link MathUtils#longToDouble(long, double)}
     *
     * @param value long类型的数值
     * @return double类型的值
     */
    public static double permillage(long value) {
        return MathUtils.longToDouble(value, MathUtils.THOUSAND);
    }

    /**
     * long类型的数值以百分比转化为double类型的值.
     * <p>
     * 参考 {@link MathUtils#longToDouble(long, double)}
     *
     * @param value long类型的数值
     * @return double类型的值
     */
    public static double percentage(long value) {
        return MathUtils.longToDouble(value, MathUtils.HUNDRED);
    }
}