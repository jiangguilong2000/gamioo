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
package io.gamioo.core.util;

import java.util.Arrays;

/**
 * 数组相关操作工具类.
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class ArrayUtils {
    /**
     * 一个空的字符串数组.
     */
    public static final String[] EMPTY_STRING_ARRAY = {};

    /**
     * <p>
     * Adds all the elements of the given arrays into a new array.
     * </p>
     * <p>
     * The new array contains all of the element of {@code array1} followed by
     * all of the elements {@code array2}. When an array is returned, it is
     * always a new array.
     * </p>
     *
     * <pre>
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1 the first array whose elements are added to the new array.
     * @param array2 the second array whose elements are added to the new array.
     * @return The new long[] array.
     */
    public static long[] addAll(final long[] array1, final long... array2) {
        final long[] joinedArray = new long[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * 判定Object数组是否为空(null或长度为0).
     *
     * @param array Object数组
     * @return 如果为空(null或长度为0)则返回true, 否则返回false.
     */
    public static boolean isEmpty(final Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判定Object数组是否不为空(null或长度为0).
     *
     * @param array Object数组
     * @return 如果不为空(null或长度为0)则返回true, 否则返回false.
     */
    public static boolean isNotEmpty(final Object[] array) {
        return !isEmpty(array);
    }

    /**
     * 判定byte数组是否为空(null或长度为0).
     *
     * @param array byte数组
     * @return 如果为空(null或长度为0)则返回true, 否则返回false.
     */
    public static boolean isEmpty(final byte[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判定byte数组是否不为空(null或长度为0).
     *
     * @param array byte数组
     * @return 如果不为空(null或长度为0)则返回true, 否则返回false.
     */
    public static boolean isNotEmpty(final byte[] array) {
        return !isEmpty(array);
    }

    /**
     * 判定boolean数组是否为空(null或长度为0).
     *
     * @param array boolean数组
     * @return 如果为空(null或长度为0)则返回true, 否则返回false.
     */
    public static boolean isEmpty(final boolean[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判定boolean数组是否不为空(null或长度为0).
     *
     * @param array boolean数组
     * @return 如果不为空(null或长度为0)则返回true, 否则返回false.
     */
    public static boolean isNotEmpty(final boolean[] array) {
        return !isEmpty(array);
    }

    /**
     * 判定int数组是否为空(null或长度为0).
     *
     * @param array int数组
     * @return 如果为空(null或长度为0)则返回true, 否则返回false.
     */
    public static boolean isEmpty(final int[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判定int数组是否不为空(null或长度为0).
     *
     * @param array int数组
     * @return 如果不为空(null或长度为0)则返回true, 否则返回false.
     */
    public static boolean isNotEmpty(final int[] array) {
        return !isEmpty(array);
    }

    /**
     * 判定long数组是否为空(null或长度为0).
     *
     * @param array long数组
     * @return 如果为空(null或长度为0)则返回true, 否则返回false.
     */
    public static boolean isEmpty(final long[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判定long数组是否不为空(null或长度为0).
     *
     * @param array long数组
     * @return 如果不为空(null或长度为0)则返回true, 否则返回false.
     */
    public static boolean isNotEmpty(final long[] array) {
        return !isEmpty(array);
    }

    /**
     * 判定float数组是否为空(null或长度为0).
     *
     * @param array float数组
     * @return 如果为空(null或长度为0)则返回true, 否则返回false.
     */
    public static boolean isEmpty(final float[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判定float数组是否不为空(null或长度为0).
     *
     * @param array float数组
     * @return 如果不为空(null或长度为0)则返回true, 否则返回false.
     */
    public static boolean isNotEmpty(final float[] array) {
        return !isEmpty(array);
    }

    /**
     * 判定double数组是否为空(null或长度为0).
     *
     * @param array double数组
     * @return 如果为空(null或长度为0)则返回true, 否则返回false.
     */
    public static boolean isEmpty(final double[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判定double数组是否不为空(null或长度为0).
     *
     * @param array double数组
     * @return 如果不为空(null或长度为0)则返回true, 否则返回false.
     */
    public static boolean isNotEmpty(final double[] array) {
        return !isEmpty(array);
    }

    /**
     * String数组转化为int数组
     *
     * @param array String数组
     * @return int数组
     */
    public static int[] toIntArray(String[] array) {
        return Arrays.stream(array).mapToInt(s -> Integer.parseInt(s)).toArray();
    }

    /**
     * Integer数组转化为int数组
     *
     * @param array Integer数组
     * @return int数组
     */
    public static int[] toIntArray(Integer[] array) {
        return Arrays.stream(array).mapToInt(Integer::intValue).toArray();
    }

    /**
     * String数组转化为long数组
     *
     * @param array String数组
     * @return long数组
     */
    public static long[] toLongArray(String[] array) {
        return Arrays.stream(array).mapToLong(s -> Long.parseLong(s)).toArray();
    }

    /**
     * Long数组转化为long数组
     *
     * @param array Long数组
     * @return long数组
     */
    public static long[] toLongArray(Long[] array) {
        return Arrays.stream(array).mapToLong(Long::longValue).toArray();
    }

    /**
     * 字符串组数转化为字节数组.
     * <p>
     * 默认使用10进制解析
     *
     * @param array 字符串组数
     * @return 转化后的字节数组
     */
    public static byte[] toByteArray(String[] array) {
        return toByteArray(array, 10);
    }

    /**
     * 字符串组数转化为字节数组.
     *
     * @param array 字符串组数
     * @param radix 角色字符串数组{@code array}时所用的进制
     * @return 转化后的字节数组
     */
    public static byte[] toByteArray(String[] array, int radix) {
        final byte[] data = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            data[i] = (byte) Integer.parseInt(array[i], radix);
        }
        return data;
    }

    /**
     * 求数组长度.
     * <p>
     * 如果数组为null，则返回0
     *
     * @param array 字符串数组
     * @return 返回字符串数组长度
     */
    public static int length(String[] array) {
        return array == null ? 0 : array.length;
    }
}