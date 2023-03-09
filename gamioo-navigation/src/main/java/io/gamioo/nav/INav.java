package io.gamioo.nav;

import io.gamioo.common.vector.Vector3f;

import java.util.List;

/**
 *
 */
public interface INav {


    /**
     * 寻找路径
     *
     * @param start 开始坐标点
     * @param end   结束坐标点
     * @return 路经典集合
     */
    List<float[]> find(float[] start, float[] end);

    List<Vector3f> find(Vector3f start, Vector3f end);


    /**
     * 光线照射发，寻找可以支线通过的hit点，如果可通过则返回hit
     *
     * @param start 开始点
     * @param end   目标点
     * @return 光照可以达到的点
     */
    float[] raycast(float[] start, float[] end);

    Vector3f raycast(Vector3f start, Vector3f end);


    /**
     * 获取指定点附近可行走的点
     *
     * @param point 当前验证点
     * @return 可以行走的点(如验证点不能行走 ， 则返回可以行走的点)
     */
    float[] findNearest(float[] point);

    Vector3f findNearest(Vector3f point);

    default float[] vector2Point(Vector3f vector3f) {
        float[] arr = new float[3];
        arr[0] = vector3f.getX();
        arr[1] = vector3f.getY();
        arr[2] = vector3f.getZ();
        return arr;
    }

    default Vector3f point2Vector(float[] point) {
        return new Vector3f(point[0], point[1], point[2]);
    }


}
