package io.gamioo.common.shape;


public interface Shape {
    /**
     * 是否包含指定的点
     *
     * @param x 点x
     * @param y 点y
     *
     * @return true:包含
     */
    boolean containsPoint(long x, long y);

    /**
     * 获取aabb包围盒
     *
     * @return 包围盒
     */
    AABB getAABB();

    /**
     * 获取图形内随机一个点
     *
     * @return 点
     */
    Point getRandomPoint();
}
