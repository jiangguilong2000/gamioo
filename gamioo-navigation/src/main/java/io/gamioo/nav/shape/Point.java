package io.gamioo.nav.shape;


import io.gamioo.nav.vector.Vector2f;

public class Point implements Shape {

    private final int x;
    private final int y;
    private AABB aabb;

    public static Point valueOf(int x, int y) {
        return new Point(x, y);
    }

    public static Point valueOf(float x, float y) {
        return new Point((int) x, (int) y);
    }

    private Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * 获取两点的距离 单位 厘米
     */
    public static double calDisBetweenTwoPoint(Point a, Point b) {
        long xDis = a.x - b.x;
        long yDis = a.y - b.y;
        return Math.sqrt(xDis * xDis + yDis * yDis);
    }

    /**
     * 绕着point 旋转 radian弧度后得到的点
     */
    public Point rotate(Point point, double radians) {
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);
        int x = (int) ((this.x - point.getX()) * cos - (this.y - point.getY()) * sin + point.getX());
        int y = (int) ((this.x - point.getX()) * sin + (this.y - point.getY()) * cos + point.getY());
        return Point.valueOf(x, y);
    }

    /**
     * 线性插值计算线段中的点
     */
    public static Point lerpPoint(Point a, Point b, float t) {
        int ax = a.getX();
        int bx = b.getX();
        int ay = a.getY();
        int by = b.getY();
        return Point.valueOf((int) (ax + (bx - ax) * t), (int) (ay + (by - ay) * t));
    }

    /**
     * 获取a点到b点 距离b点 指定距离的点
     */
    public static Point getPointWithDisToEndPoint(Point a, Point b, int dis) {
        int ax = a.getX();
        int bx = b.getX();
        int ay = a.getY();
        int by = b.getY();
        double abDis = Math.sqrt((ax - bx) * (ax - bx) + (ay - by) * (ay - by));
        // 超出距离 则后退
//        if (abDis < dis) {
//            throw new ServiceException("a:{} b:{} dis:{}", a, b, dis);
//        }
        return lerpPoint(a, b, (float) ((abDis - dis) / abDis));
    }

    /**
     * 获取a点到b点 距离a点 指定距离的点
     */
    public static Point getPointWithDisToSrcPoint(Point a, Point b, float dis) {
        long ax = a.getX();
        long bx = b.getX();
        long ay = a.getY();
        long by = b.getY();
        float abDis = (float) Math.sqrt((ax - bx) * (ax - bx) + (ay - by) * (ay - by));
//        if (abDis < dis) {
//            throw new ServiceException("a:{} b:{} dis:{}", a, b, dis);
//        }
        return lerpPoint(a, b, dis / abDis);
    }

    /**
     * 通过起点、向量、距离获取终点
     */
    public Point getPointWithVectorAndDis(Vector2f vector, float dis) {
        Vector2f newVector = vector.resizeLength(dis);
        return Point.valueOf(x + (int) newVector.getX(), y + (int) newVector.getY());
    }

    @Override
    public boolean containsPoint(long x, long y) {
        return this.x == x && this.y == y;
    }

    @Override
    public AABB getAABB() {
        if (aabb == null) {
            aabb = new AABB(x, y, x, y);
        }
        return aabb;
    }

    @Override
    public Point getRandomPoint() {
        return Point.valueOf(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Point)) {
            return false;
        }
        Point point = (Point) obj;
        return this.x == point.x && this.y == point.y;
    }

    /**
     * 大体上相同 允许精度误差
     */
    public boolean roughlyEquals(Point point, final int accuracyErrorSize) {
        if (this == point) {
            return true;
        }
        return Math.abs(point.getX() - getX()) < accuracyErrorSize && Math.abs(point.getY() - getY()) < accuracyErrorSize;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}