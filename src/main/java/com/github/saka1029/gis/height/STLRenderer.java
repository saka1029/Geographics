package com.github.saka1029.gis.height;

import com.github.saka1029.gis.stl.Point3;
import com.github.saka1029.gis.stl.STL;

public class STLRenderer {

    static final double UNIT = 3.0;
    static final int DEPTH = 2;
    static final double SCALE = 10.0;

    final DB db;
    final long minLon, minLat, maxLon, maxLat;
    final double minHeight, maxHeight;
    final STL stl;

    STLRenderer(DB db, double minLon, double minLat, double maxLon, double maxLat) {
        if (db.get(minLon, minLat) == 0)
            throw new IllegalArgumentException("(" + minLon + ", " + minLat + ") not exists");
        if (db.get(maxLon, maxLat) == 0)
            throw new IllegalArgumentException("(" + minLon + ", " + minLat + ") not exists");
        this.db = db;
        this.minLon = Math.min(db.ppp(minLon), db.ppp(maxLon));
        this.minLat = Math.min(db.qqq(minLat), db.qqq(maxLat));
        this.maxLon = Math.max(db.ppp(minLon), db.ppp(maxLon));
        this.maxLat = Math.max(db.qqq(minLat), db.qqq(maxLat));
        String name = String.format("(%f, %f) - (%f, %f)", minLon, minLat, maxLon, maxLat);
        double h = HeightEnum.height(db.get(minLon, minLat));
        double minHeight = h;
        double maxHeight = h;
        for (long p = this.minLon; p <= this.maxLon; ++p)
            for (long q = this.minLat; q <= this.maxLat; ++q) {
                int typeHeight = db.get(p, q);
                HeightEnum type = HeightEnum.type(typeHeight);
                if (isSurface(type)) {
                    double x = HeightEnum.height(typeHeight);
                    minHeight = Math.min(minHeight, x);
                    maxHeight = Math.max(maxHeight, x);
                }
            }
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.stl = new STL(name);
    }

    static boolean isSurface(HeightEnum t) {
        switch (t) {
        case 地表面:
        case 表層面:
        case その他:
            return true;
        default:
            return false;
        }
    }

    Point3 pointTop(long p, long q) {
        int typeHeight = db.get(p, q);
        HeightEnum type = HeightEnum.type(typeHeight);
        double height = HeightEnum.height(typeHeight);
        if (!isSurface(type)) height = minHeight - DEPTH;
        return new Point3((p - minLon) * UNIT, (maxLat - q - 1) * UNIT, (height - minHeight + DEPTH) * SCALE);
    }

    Point3 pointBottom(long p, long q) {
        return new Point3((p - minLon) * UNIT, (maxLat - q - 1) * UNIT, 0);
    }

    void renderTop() {
        for (long p = minLon + 1; p <= maxLon; ++p) {
            for (long q = minLat + 1; q <= maxLat; ++q) {
                Point3 nw = pointTop(p - 1, q - 1);
                Point3 ne = pointTop(p, q - 1);
                Point3 sw = pointTop(p - 1, q);
                Point3 se = pointTop(p, q);
                stl.add(nw, ne, se);
                stl.add(nw, se, sw);
            }
        }
    }

    void renderBottom() {
        Point3 nw = pointBottom(minLon, minLat);
        Point3 ne = pointBottom(maxLon, minLat);
        Point3 sw = pointBottom(minLon, maxLat);
        Point3 se = pointBottom(maxLon, maxLat);
        stl.add(nw, ne, se);
        stl.add(nw, se, sw);
    }

    void renderNorth() {
        for (long p = minLon + 1; p <= maxLon; ++p) {
            Point3 tw = pointTop(p - 1, minLat);
            Point3 te = pointTop(p, minLat);
            Point3 bw = pointBottom(p - 1, minLat);
            Point3 be = pointBottom(p, minLat);
            stl.add(tw, te, be);
            stl.add(tw, be, bw);
        }
    }

    void renderSouth() {
        for (long p = minLon + 1; p <= maxLon; ++p) {
            Point3 tw = pointTop(p - 1, maxLat);
            Point3 te = pointTop(p, maxLat);
            Point3 bw = pointBottom(p - 1, maxLat);
            Point3 be = pointBottom(p, maxLat);
            stl.add(tw, be, te);
            stl.add(tw, bw, be);
        }
    }

    void renderWest() {
        for (long q = minLat + 1; q <= maxLat; ++q) {
            Point3 tn = pointTop(minLon, q - 1);
            Point3 ts = pointTop(minLon, q);
            Point3 bn = pointBottom(minLon, q - 1);
            Point3 bs = pointBottom(minLon, q);
            stl.add(tn, ts, bs);
            stl.add(tn, bs, bn);
        }
    }

    void renderEast() {
        for (long q = minLat + 1; q <= maxLat; ++q) {
            Point3 tn = pointTop(maxLon, q - 1);
            Point3 ts = pointTop(maxLon, q);
            Point3 bn = pointBottom(maxLon, q - 1);
            Point3 bs = pointBottom(maxLon, q);
            stl.add(tn, bs, ts);
            stl.add(tn, bn, bs);
        }
    }

    STL render() {
        System.out.printf("(%d, %d) - (%d, %d)%n", minLon, minLat, maxLon, maxLat);
        System.out.printf("size: (%d, %d)%n", maxLon - minLon, maxLat - minLat);
        System.out.println(stl.name);
        renderTop();
        renderBottom();
        renderNorth();
        renderSouth();
        renderWest();
        renderEast();
        return stl;
    }

    public static STL render(DB db, double minLon, double minLat, double maxLon, double maxLat) {
        return new STLRenderer(db, minLon, minLat, maxLon, maxLat).render();
    }
}
