package gis.stl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.saka1029.gis.stl.Point3;
import com.github.saka1029.gis.stl.Triangle;

class TestTriangle {

    @Test
    void test() {
        Triangle t = new Triangle(
            new Point3(1, 0, 0),
            new Point3(0, 2, 0),
            new Point3(0, 0, 3));
        assertEquals(new Point3(6D, 3D, 2D).divide(7D), t.normalUnitVector());
    }

}
