package io.gamioo.nav.shape;

/**
 * @author Allen Jiang
 */
public class AABB implements Shape {
    private final int left;
    private final int top;
    private final int right;
    private final int bottom;


    public AABB(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }


    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
    }

    public int getCenterX() {
        return (left + right + 1) / 2;
    }

    public int getCenterY() {
        return (top + bottom + 1) / 2;
    }

    public int getWidth() {
        return right - left + 1;
    }

    public int getHeight() {
        return bottom - top + 1;
    }


    @Override
    public String toString() {
        return "AABB{" +
                "left=" + left +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                '}';
    }

    @Override
    public boolean containsPoint(long x, long y) {
        return left <= x && x <= right && top <= y && y <= bottom;
    }

    @Override
    public AABB getAABB() {
        return this;
    }

    @Override
    public Point getRandomPoint() {
        return null;
    }
}