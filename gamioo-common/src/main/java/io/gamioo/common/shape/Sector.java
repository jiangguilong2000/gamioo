package io.gamioo.common.shape;

import io.gamioo.common.util.MathUtils;
import io.gamioo.common.vector.Vector2f;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 扇形
 *
 * @author Allen Jiang
 */
public class Sector implements Shape {
    private Point center;
    private Point pointA;
    private Point pointB;
    private final int r;
    private final int startAngle;
    private final int endAngle;

    private AABB aabb = null;

    /**
     * 扇形构造函数
     *
     * @param one    原点
     * @param other  目标点
     * @param radius 半径
     * @param angle  扇形全角
     * @return know 形实例
     */
    public static Sector valueOf(Point one, Point other, int radius, int angle) {
        return new Sector(one, other, radius, angle);
    }

    private Sector(Point one, Point other, int radius, int angle) {
        this.center = Point.valueOf(one.getX(), one.getY());
        this.r = radius;
        Vector2f base = Vector2f.getVectorFromPointToPoint(one, other);
        Vector2f left = base.rotate(angle / 2).resizeLength(radius);
        this.pointA = Point.valueOf(left.getX() + this.center.getX(), left.getY() + this.center.getY());
        Vector2f right = left.rotate(-angle);
        this.pointB = Point.valueOf(right.getX() + this.center.getX(), right.getY() + this.center.getY());
        this.startAngle = (int) left.getAngle();
        this.endAngle = this.startAngle + angle;


    }

    @Override
    public boolean containsPoint(long x, long y) {
        return pointWithinRadius(x, y) && angleContainPoint(x, y);
    }

    /**
     * 忽略角度判断某点是否在扇形半径内
     * @param x 坐标x
     * @param y 坐标y
     * @return true 坐标在bigint形范围内 ; false  坐标在bigint形范围外
     */
    public boolean pointWithinRadius(long x, long y) {
        long distance = (x - this.center.getX()) * (x - this.center.getX()) + (y - this.center.getY()) * (y - this.center.getY());
        return distance <= (long) r * r;
    }

    public boolean containsPoint(Point point) {
        return this.containsPoint(point.getX(), point.getY());
    }


    @Override
    public AABB getAABB() {
        if (aabb == null) {
            List<Integer> listX = new ArrayList<>();
            List<Integer> listY = new ArrayList<>();
            Point center = this.center;
            listX.add(center.getX());
            listY.add(center.getY());

            Point pointA = this.pointA;
            listX.add(pointA.getX());
            listY.add(pointA.getY());

            Point pointB = this.pointB;
            listX.add(pointB.getX());
            listY.add(pointB.getY());

            Point pointC = Point.valueOf(this.center.getX() - r, this.center.getY());
            Point pointD = Point.valueOf(this.center.getX() + r, this.center.getY());

            Point pointE = Point.valueOf(this.center.getX(), this.center.getY() + r);
            Point pointF = Point.valueOf(this.center.getX(), this.center.getY() - r);


            if (this.containsPoint(pointC)) {
                listX.add(pointC.getX());
                listY.add(pointC.getY());
            }
            if (this.containsPoint(pointD)) {
                listX.add(pointD.getX());
                listY.add(pointD.getY());
            }
            if (this.containsPoint(pointE)) {
                listX.add(pointE.getX());
                listY.add(pointE.getY());
            }
            if (this.containsPoint(pointF)) {
                listX.add(pointF.getX());
                listY.add(pointF.getY());
            }

            int left = MathUtils.min(listX);
            int right = MathUtils.max(listX);
            int top = MathUtils.min(listY);
            int bottom = MathUtils.max(listY);
            aabb = new AABB(left, top, right, bottom);
        }
        return aabb;

    }

    @Override
    public Point getRandomPoint() {
        int length = RandomUtils.nextInt(1, r);
        int angle;
        if (this.startAngle < 0) {
            angle = (int) RandomUtils.nextDouble(this.startAngle + 1 + 360, this.endAngle + 360);
        } else {
            angle = (int) RandomUtils.nextDouble(this.startAngle + 1, this.endAngle);
        }
        Vector2f result = Vector2f.valueOf(length, angle);
        return Point.valueOf(result.getX() + this.center.getX(), result.getY() + this.center.getY());
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public Point getPointA() {
        return pointA;
    }

    public void setPointA(Point pointA) {
        this.pointA = pointA;
    }

    public Point getPointB() {
        return pointB;
    }

    public void setPointB(Point pointB) {
        this.pointB = pointB;
    }

    public int getR() {
        return r;
    }

    public double getStartAngle() {
        return startAngle;
    }

    public double getEndAngle() {
        return endAngle;
    }

    /**
     * 判断点是否在扇形夹角中
     *
     * @param x x坐标
     * @param y y坐标
     * @return true表示在扇形夹角中
     */
    public boolean angleContainPoint(long x, long y) {
        double angle = Math.atan2(y - getCenter().getY(), x - getCenter().getX());
        angle = Math.toDegrees(angle);
        return angle >= startAngle && angle <= endAngle;
    }
}
