package com.github.saka1029.gis.stl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {

    public final Point3 a, b, c;

    public Triangle(Point3 a, Point3 b, Point3 c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    /**
     * 法線単位ベクトルを求めます。
     */
    public Point3 normalUnitVector() {
        Point3 ab = b.subtract(a);
        Point3 ac = c.subtract(a);
        return ab.outerProduct(ac).unit();
    }

    public void writeTo(PrintWriter writer) {
        Point3 n = normalUnitVector();
        writer.printf("  facet normal %f %f %f" + STL.NL, n.x, n.y, n.z);
        writer.printf("    outer loop" + STL.NL);
        writer.printf("      vertex %f %f %f" + STL.NL, a.x, a.y, a.z);
        writer.printf("      vertex %f %f %f" + STL.NL, b.x, b.y, b.z);
        writer.printf("      vertex %f %f %f" + STL.NL, c.x, c.y, c.z);
        writer.printf("    endloop" + STL.NL);
        writer.printf("  endfacet" + STL.NL);
    }

    /**
     * doubleの値をflotに変換し、さらにリトルエンディアンのintに変換します。
     */
    static int little(double d) {
        return ByteBuffer.allocate(4)
            .putFloat((float)d)
            .order(ByteOrder.LITTLE_ENDIAN)
            .flip()
            .getInt();
    }


    /**
     * この実装はByteBufferを使用するものと比べて6倍遅い。
     */
//    void writeTo(Point3 p, DataOutputStream out) throws IOException {
//        out.writeInt(Integer.reverseBytes(Float.floatToIntBits((float)p.x)));
//        out.writeInt(Integer.reverseBytes(Float.floatToIntBits((float)p.y)));
//        out.writeInt(Integer.reverseBytes(Float.floatToIntBits((float)p.z)));
//    }

    void writeTo(Point3 p, DataOutputStream out) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(Float.BYTES * 3);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put((float)p.x).put((float)p.y).put((float)p.z);
        fb.flip();
        out.write(bb.array());
    }

    public void writeTo(DataOutputStream out) throws IOException {
        Point3 n = normalUnitVector();
        writeTo(n, out);
        writeTo(a, out);
        writeTo(b, out);
        writeTo(c, out);
        out.writeBytes("  ");
    }
}
