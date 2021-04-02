/*
 * Copyright 2015-2020 yorha Authors.
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
package io.gamioo.core.lang;

/**
 * 平面坐标系中的一个点.
 *
 */
public class Point {
    private final int x;
    private final int y;

    private Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 根据X与Y构建一个坐标点.
     * <p>
     * 这个类未公开构造函数，是为了日后方便此类做缓存处理，入口先准备好
     *
     * @param x X坐标
     * @param y Y坐标
     * @return 坐标点
     */
    public static Point valueOf(int x, int y) {
        return new Point(x, y);
    }

    /**
     * 获取X坐标
     *
     * @return X坐标
     */
    public int getX() {
        return x;
    }

    /**
     * 获取Y坐标
     *
     * @return Y坐标
     */
    public int getY() {
        return y;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Point other = (Point) obj;
        if (x != other.x) {
            return false;
        }
        if (y != other.y) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Point [x=" + x + ", y=" + y + "]";
    }
}