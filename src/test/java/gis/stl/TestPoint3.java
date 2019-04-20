package gis.stl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.saka1029.gis.stl.Point3;

class TestPoint3 {

    @Test
    void test() {
        Point3 x = new Point3(1, 0, 0);
        Point3 y = new Point3(0, 2, 0);
        Point3 z = new Point3(0, 0, 3);
        Point3 xy = y.subtract(x);
        assertEquals(new Point3(-1, 2, 0), xy);
        Point3 xz = z.subtract(x);
        assertEquals(new Point3(-1, 0, 3), xz);
        Point3 xy_xz = xy.outerProduct(xz);
        assertEquals(new Point3(6, 3, 2), xy_xz);
        double length = xy_xz.length();
        assertEquals(7.0D, length);
        Point3 unit = xy_xz.divide(length);
        assertEquals(new Point3(6D / 7D, 3D / 7D, 2D / 7D), unit);
        assertEquals(new Point3(6D / 7D, 3D / 7D, 2D / 7D), xy_xz.unit());
    }

}
