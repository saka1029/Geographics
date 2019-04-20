package com.github.saka1029.gis.stl;

public class Point3 {

    public static final Point3 ORIGIN = new Point3(0, 0, 0);

    public final double x, y, z;

    public Point3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * 各座標の符号を反転た座標を求めます。
     */
    public Point3 negate() {
        return new Point3(-x, -y, -z);
    }

    /**
     * ベクトルとしての長さを求めます。
     */
    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * 単位ベクトルを求めます。
     */
    public Point3 unit() {
        return divide(length());
    }

    public Point3 add(double k) {
        return new Point3(x + k, y + k, z + k);
    }

    public Point3 add(Point3 right) {
        return new Point3(x + right.x, y + right.y, z + right.z);
    }

    public Point3 subtract(double k) {
        return new Point3(x - k, y - k, z - k);
    }

    public Point3 subtract(Point3 right) {
        return new Point3(x - right.x, y - right.y, z - right.z);
    }

    public Point3 multiply(double k) {
        return new Point3(k * x, k * y, k * z);
    }

    public Point3 multiply(Point3 right) {
        return new Point3(x * right.x, y * right.y, z * right.z);
    }

    public Point3 divide(double k) {
        return new Point3(x / k, y / k, z / k);
    }

    public Point3 divide(Point3 right) {
        return new Point3(x / right.x, y / right.y, z / right.z);
    }

    public double innerProduct(Point3 right) {
        return x * right.x + y * right.y + z * right.z;
    }

    public Point3 outerProduct(Point3 right) {
        return new Point3(
            y * right.z - z * right.y,
            z * right.x - x * right.z,
            x * right.y - y * right.x);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != Point3.class)
            return false;
        Point3 o = (Point3)obj;
        return x == o.x && y == o.y && z == o.z;
    }

    @Override
    public String toString() {
        return String.format("(%f, %f, %f)", x, y, z);
    }
}
