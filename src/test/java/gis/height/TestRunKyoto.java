package gis.height;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import com.github.saka1029.gis.common.Logging;
import com.github.saka1029.gis.height.DB;
import com.github.saka1029.gis.height.Zoomer;

class TestRunKyoto {

    static Logger logger = Logging.logger(TestRunKyoto.class);

    static final File BASE_DIR = new File("D:/JPGIS");
    static final File INPUT_DIR = new File(BASE_DIR, "kyoto/GML");
    static final File DB_DIR = new File(BASE_DIR, "kyoto/BIN");
    static final int Z = 15;
    static final File IMAGE_DIR = new File(BASE_DIR, "height5m/tokyo-height/image/theme1");
    static final File IMAGE_DIR_15 = new File(IMAGE_DIR, "15");
    static final int CACHE_SIZE = 100;

    @Test
    public void main() throws IOException, SAXException, ParserConfigurationException {
        logger.info("start");
        try (DB db = new DB(DB_DIR, Z, CACHE_SIZE)) {
//            Parser.parse(INPUT_DIR, db);
//            Interpolator.interpolate(db);
//            Renderer.render(db, IMAGE_DIR_15);
//            Debug.renderHtml(db, IMAGE_DIR_15);
        }
        for (int z = 16; z <= 16; ++z)
            Zoomer.zoom(IMAGE_DIR_15, new File(IMAGE_DIR, Integer.toString(z)), z);
        for (int z = 14; z >= 10; --z)
            Zoomer.zoom(IMAGE_DIR_15, new File(IMAGE_DIR, Integer.toString(z)), z);
        logger.info("end");
    }
}
