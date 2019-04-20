package gis.height;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import com.github.saka1029.gis.height.Zoomer;

class TestZoomer {

	static final File BASE_DIR = new File("data/interpolated");
    static final File IMAGE_DIR = new File(BASE_DIR, "image");
    static final File INPUT_DIR = new File(IMAGE_DIR, "15");

    @Ignore
    @Test
    void testZoom() throws IOException {
      for (int z = 16; z <= 16; ++z)
          Zoomer.zoom(INPUT_DIR, new File(IMAGE_DIR, Integer.toString(z)), z);
      for (int z = 14; z >= 10; --z)
          Zoomer.zoom(INPUT_DIR, new File(IMAGE_DIR, Integer.toString(z)), z);
    }

}
