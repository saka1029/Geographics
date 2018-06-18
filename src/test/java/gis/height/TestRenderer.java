package gis.height;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.github.saka1029.gis.height.DB;
import com.github.saka1029.gis.height.Debug;
import com.github.saka1029.gis.height.Renderer;

public class TestRenderer {

    static File BASE_DIR = new File("data/interpolated");
    static File DB_DIR = new File(BASE_DIR, "db");
    static File IMAGE_DIR = new File(BASE_DIR, "image/15");
    static int Z = 15;
    static int CACHE_SIZE = 100;

    @Test
    public void testInterpolate() throws IOException {
        if (!IMAGE_DIR.exists())
            IMAGE_DIR.mkdirs();
        try (DB db = new DB(DB_DIR, Z, CACHE_SIZE)) {
            Renderer.render(db, IMAGE_DIR);
            Debug.renderHtml(db, IMAGE_DIR);
        }
    }

}
