package com.github.saka1029.gis.stl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class STL {

    public final String name;
    public final List<Triangle> triangles = new ArrayList<>();

    public STL(String name) {
        this.name = name;
    }

    public STL(String name, Triangle... triangles) {
        this(name);
        for (Triangle t : triangles)
            this.triangles.add(t);
    }

    public STL add(Point3 a, Point3 b, Point3 c) {
        triangles.add(new Triangle(a, b, c));
        return this;
    }

    public STL add(Point3 a, Point3 b, Point3 c, Point3 d) {
        triangles.add(new Triangle(a, b, c));
        triangles.add(new Triangle(a, c, d));
        return this;
    }

    public static final String NL = "\r\n";

    public void writeTo(PrintWriter writer) {
        writer.printf("solid %s" + NL, name);
        for (Triangle triangle : triangles)
            triangle.writeTo(writer);
        writer.printf("endsolid %s" + NL, name);
    }

    public void writeTo(DataOutputStream out) throws IOException {
        String n = String.format("%-80s", name);
        if (n.length() > 80) n = n.substring(0, 80);
        out.writeBytes(n);
        out.writeInt(Integer.reverseBytes(triangles.size()));
        for (Triangle triangle : triangles)
            triangle.writeTo(out);
    }
}
