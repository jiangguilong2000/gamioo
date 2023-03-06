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

/**
 * 字节数组操作工具类.
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class ByteArrayUtils {
    /**
     * 一个空的字节数组.
     */
    public static final byte[] EMPTY_BYTE_ARRAY = {};

    /**
     * 一个short类型的数字转化为2位byte数组
     *
     * @param a short类型的数字
     * @return byte数组
     */
    public static byte[] toByteArray(short a) {
        return new byte[]{(byte) ((a >> 8) & 0xFF), (byte) (a & 0xFF)};
    }

    /**
     * 一个int类型的数字转化为4位byte数组
     *
     * @param num int类型的数字
     * @return byte数组
     */
    public static byte[] toByteArray(int num) {
        return new byte[]{(byte) ((num >> 24) & 0xFF), (byte) ((num >> 16) & 0xFF), (byte) ((num >> 8) & 0xFF), (byte) (num & 0xFF)};
    }

    /**
     * 4位byte数组转化为一个int类型的数字
     *
     * @param bytes byte数组
     * @return int类型的数字
     */
    public static int toInt(byte[] bytes) {
        return bytes[3] & 0xFF | (bytes[2] & 0xFF) << 8 | (bytes[1] & 0xFF) << 16 | (bytes[0] & 0xFF) << 24;
    }
}