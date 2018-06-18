package gis.height;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import com.github.saka1029.gis.height.Zoomer;

class TestZoomer {

	static final File base = new File("D:/JPGIS/height5m/tokyo-height/image/theme1");
    static final File inDir = new File(base, "15");

    @Ignore
    @Test
    void testZoom() throws IOException {
      for (int z = 16; z <= 16; ++z)
          Zoomer.zoom(inDir, new File(base, Integer.toString(z)), z);
      for (int z = 14; z >= 10; --z)
          Zoomer.zoom(inDir, new File(base, Integer.toString(z)), z);
//      for (int z = 13; z >= 10; --z)
//          new Zoomer().zoom(inDir, new File(base, Integer.toString(z)), z);
    }

    @Ignore
    @Test
    void testZoomOut() throws IOException {
        Zoomer.zoom(inDir, new File(base, "10"), 14);
    }

}
