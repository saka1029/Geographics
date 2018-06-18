package gis.height;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.github.saka1029.gis.common.Util;
import com.github.saka1029.gis.height.DB;
import com.github.saka1029.gis.height.Debug;
import com.github.saka1029.gis.height.Interpolator;

public class TestInterpolator {

    static File dbDir = new File("data/parsed/db");
    static File interpolatedDir = new File("data/interpolated");
    static File interpolatedDbDir = new File(interpolatedDir, "db");
    static File interpolatedDumpDir = new File(interpolatedDir, "dump");
    static int z = 15;
    static int cacheSize = 100;

    @Test
    public void testInterpolate() throws IOException {
        if (!interpolatedDbDir.exists())
            interpolatedDbDir.mkdirs();
        if (!interpolatedDumpDir.exists())
            interpolatedDumpDir.mkdirs();
        for (File file : dbDir.listFiles(f -> f.getName().toLowerCase().endsWith(".bin")))
            Util.copy(file, interpolatedDbDir);
        try (DB db = new DB(interpolatedDbDir, z, cacheSize)) {
            Interpolator.interpolate(db);
            Debug.dump(db, interpolatedDumpDir);
            Debug.renderHtml(db, interpolatedDumpDir);
        }
    }

}
