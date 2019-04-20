package gis.height;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.saka1029.gis.height.DB;
import com.github.saka1029.gis.height.STLRenderer;
import com.github.saka1029.gis.stl.STL;

class TestSTLRenderer {

    @Test
    void testHonancho() throws IOException {
        File dbDir = new File("data/interpolated/db");
        File stlFile = new File("data/stl/honancho.stl");
        if (!stlFile.getParentFile().exists())
            stlFile.getParentFile().mkdirs();
        int z = 15;
        int cacheSize = 100;
        double minLon = 139.648588;
        double minLat = 35.677203;
        double maxLon = 139.666583;
        double maxLat = 35.688852;
        long start = System.currentTimeMillis();
        try (DB db = new DB(dbDir, z, cacheSize)) {
            STL stl = STLRenderer.render(db, minLon, minLat, maxLon, maxLat);
            long endRender = System.currentTimeMillis();
            try (DataOutputStream w = new DataOutputStream(new FileOutputStream(stlFile))) {
                stl.writeTo(w);
            }
            long end = System.currentTimeMillis();
            System.out.println("render: " + (endRender - start) + " write: " + (end - endRender));
        }
    }

    @Test
    void testShinjuku() throws IOException {
        File dbDir = new File("D:/JPGIS/height5m/BIN");
        File stlFile = new File("data/stl/shinjuku.stl");
        if (!stlFile.getParentFile().exists())
            stlFile.getParentFile().mkdirs();
        int z = 15;
        int cacheSize = 100;
        double minLon = 139.670581;
        double minLat = 35.676401;
        double maxLon = 139.751761;
        double maxLat = 35.730934;
        long start = System.currentTimeMillis();
        try (DB db = new DB(dbDir, z, cacheSize)) {
            STL stl = STLRenderer.render(db, minLon, minLat, maxLon, maxLat);
            long endRender = System.currentTimeMillis();
            try (DataOutputStream w = new DataOutputStream(new FileOutputStream(stlFile))) {
                stl.writeTo(w);
            }
            long end = System.currentTimeMillis();
            System.out.println("render: " + (endRender - start) + " write: " + (end - endRender));
        }
    }

}
