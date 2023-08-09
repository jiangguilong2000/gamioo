package io.gamioo.common.vector;

import io.gamioo.common.shape.Point;


/**
 * @author Allen Jiang
 */
public class Vector2f {
    private final float x;
    private final float y;

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public double getRadians() {
        return Math.atan2(y, x);
    }

    public Point toPoint() {
        return Point.valueOf(x, y);
    }

    public double getAngle() {
        return Math.toDegrees(this.getRadians());
    }

    public double getLength() {
        return Math.sqrt(x * x + y * y);
    }


    public static Vector2f valueOf(int length, int angle) {
        double radians = Math.toRadians(angle);
        float y = (float) Math.sin(radians) * length;
        float x = (float) Math.cos(radians) * length;
        return new Vector2f(x, y);
    }


    public static Vector2f valueOf(float x, float y) {
        return new Vector2f(x, y);
    }

    public static Vector2f add(Vector2f a, Vector2f b) {
        return new Vector2f(a.x + b.x, a.y + b.y);
    }

    public Vector2f add(Vector2f src) {
        return new Vector2f(x + src.x, y + src.y);
    }

    public Point add(Point srcPoint) {
        return Point.valueOf(x + srcPoint.getX(), y + srcPoint.getY());
    }


    /**
     * 单位化
     *
     * @param a 向量
     * @return 调整后的向量
     */
    public static Vector2f unitization(Vector2f a) {
        float len = (float) a.getLength();
        return new Vector2f(a.getX() / len, a.getY() / len);
    }

    public Vector2f unitization() {
        return unitization(this);
    }

    /**
     * 调整长度
     *
     * @param newLength 新长度
     * @return 调整后的向量
     */
    public Vector2f resizeLength(float newLength) {
        float len = (float) getLength();
        return Vector2f.valueOf(x / len * newLength, y / len * newLength);
    }

    /**
     * 旋转
     *
     * @param angle 顺时针 旋转角度 [0 - 360]
     * @return 旋转后的向量
     */
    public Vector2f rotate(int angle) {
        double radians = Math.toRadians(angle);
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);
        return Vector2f.valueOf((float) (sin * y + cos * x), (float) (cos * y - sin * x));
    }


    public Vector2f rotate(double radians) {
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);
        return Vector2f.valueOf((float) (sin * y + cos * x), (float) (cos * y - sin * x));
    }

    /**
     * 绕着point 旋转 radian弧度后得到的点
     *
     * @param point   点
     * @param radians 弧度
     * @return 旋转后的向量
     */
    public Vector2f rotate(Vector2f point, double radians) {
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);
        int x = (int) ((this.x - point.getX()) * cos - (this.y - point.getY()) * sin + point.getX());
        int y = (int) ((this.x - point.getX()) * sin + (this.y - point.getY()) * cos + point.getY());
        return Vector2f.valueOf(x, y);
    }

    public static Vector2f getVectorFromPointToPoint(Point srcPoint, Point endPoint) {
        return new Vector2f(endPoint.getX() - srcPoint.getX(), endPoint.getY() - srcPoint.getY());
    }

    public static Vector2f getVectorFromPointToPoint(int srcX, int srcY, int endX, int endY) {
        return new Vector2f(endX - srcX, endY - srcY);
    }

    /**
     * 向量点乘
     *
     * @param a 向量1
     * @param b 向量2
     * @return 返回结果
     */
    public static float dotProduct(Vector2f a, Vector2f b) {
        return a.getX() * b.getX() + a.getY() * b.getY();
    }

    /**
     * 向量叉乘
     *
     * @param a 向量1
     * @param b 向量2
     * @return 返回结果
     */
    public static float crossProduct(Vector2f a, Vector2f b) {
        return a.getX() * b.getY() - a.getY() * b.getX();
    }

    /**
     * 获取两个向量的夹角
     *
     * @param a 向量1
     * @param b 向量2
     * @return 返回值 [0 - 180]
     */
    public static float getIntersectionAngle(Vector2f a, Vector2f b) {
        float v = dotProduct(a, b);
        double value = v / (a.getLength() * b.getLength());
        double angle = Math.toDegrees(Math.acos(value));
        return (float) angle;
    }

    /**
     * 获取两个向量的夹角
     * 以a为基准，顺时针计算到b的夹角
     *
     * @param a 向量1
     * @param b 向量2
     * @return 返回值 [0 - 360]
     */
    public static float getIntersectionAngle2(Vector2f a, Vector2f b) {
        double v = Math.atan2(a.getY(), a.getX()) - Math.atan2(b.getY(), b.getX());
        double angle = Math.toDegrees(v);
        if (angle > 360) {
            angle -= 360;
        } else if (angle < 0) {
            angle += 360;
        }

        return (float) angle;
    }
}
