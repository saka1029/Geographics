package gis.stl;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.jupiter.api.Test;

import com.github.saka1029.gis.stl.Point3;
import com.github.saka1029.gis.stl.STL;
import com.github.saka1029.gis.stl.Triangle;

class TestSTL {

    static STL cube() {
        Point3 p000 = new Point3(0, 0, 0);
        Point3 p010 = new Point3(0, 1, 0);
        Point3 p110 = new Point3(1, 1, 0);
        Point3 p100 = new Point3(1, 0, 0);
        Point3 p001 = new Point3(0, 0, 1);
        Point3 p011 = new Point3(0, 1, 1);
        Point3 p111 = new Point3(1, 1, 1);
        Point3 p101 = new Point3(1, 0, 1);
        return new STL("cube",
            new Triangle(p000, p100, p101),
            new Triangle(p000, p101, p001),
            new Triangle(p010, p111, p110),
            new Triangle(p010, p011, p111),
            new Triangle(p000, p001, p010),
            new Triangle(p010, p001, p011),
            new Triangle(p100, p110, p101),
            new Triangle(p110, p111, p101),
            new Triangle(p000, p110, p100),
            new Triangle(p000, p010, p110),
            new Triangle(p001, p101, p111),
            new Triangle(p001, p111, p011)
        );
    }

    @Test
    void testCubeASCII() throws IOException {
        STL cube = cube();
        File out = new File("data/stl/cube.stl");
        if (!out.getParentFile().exists())
            out.getParentFile().mkdirs();
        try (PrintWriter w = new PrintWriter(new FileWriter(out))) {
            cube.writeTo(w);
        }
    }

    @Test
    void testCubeBinary() throws IOException {
        STL cube = cube();
        File out = new File("data/stl/cube-bin.stl");
        if (!out.getParentFile().exists())
            out.getParentFile().mkdirs();
        try (DataOutputStream w = new DataOutputStream(new FileOutputStream(out))) {
            cube.writeTo(w);
        }
    }

    @Test
    public void testCubeBySquare() throws IOException {
        Point3 p000 = new Point3(0, 0, 0);
        Point3 p010 = new Point3(0, 1, 0);
        Point3 p110 = new Point3(1, 1, 0);
        Point3 p100 = new Point3(1, 0, 0);
        Point3 p001 = new Point3(0, 0, 1);
        Point3 p011 = new Point3(0, 1, 1);
        Point3 p111 = new Point3(1, 1, 1);
        Point3 p101 = new Point3(1, 0, 1);
        STL cube = new STL("cube by square");
        cube.add(p000, p100, p101, p001);
        cube.add(p010, p011, p111, p110);
        cube.add(p000, p001, p011, p010);
        cube.add(p100, p110, p111, p101);
        cube.add(p001, p101, p111, p011);
        cube.add(p000, p010, p110, p100);
        File out = new File("data/stl/cube-by-square.stl");
        if (!out.getParentFile().exists())
            out.getParentFile().mkdirs();
        try (DataOutputStream w = new DataOutputStream(new FileOutputStream(out))) {
            cube.writeTo(w);
        }
    }

    @Test
    public void testWing() throws IOException {
        Point3 p000 = new Point3(0, 0, 0);
        Point3 p101 = new Point3(1, 0, 1);
        Point3 p110 = new Point3(1, 1, 0);
        Point3 p011 = new Point3(0, 1, 1);
        STL wing = new STL("wing");
        wing.add(p000, p101, p110, p011);
        File out = new File("data/stl/wing.stl");
        if (!out.getParentFile().exists())
            out.getParentFile().mkdirs();
        try (DataOutputStream w = new DataOutputStream(new FileOutputStream(out))) {
            wing.writeTo(w);
        }
    }

}
