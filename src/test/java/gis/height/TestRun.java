package gis.height;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.github.saka1029.gis.common.Logging;
import com.github.saka1029.gis.height.DB;
import com.github.saka1029.gis.height.Debug;

public class TestRun {

    static Logger logger = Logging.logger(TestRun.class);

    static final File BASE_DIR = new File("D:/JPGIS/height5m");
    static final File INPUT_DIR = new File(BASE_DIR, "GML");
    static final File DB_DIR = new File(BASE_DIR, "BIN");
    static final int Z = 15;
    static final File IMAGE_DIR = new File(BASE_DIR, "tokyo-height/image/theme1/" + Z);
    static final int CACHE_SIZE = 100;

    @Test
    public void main() throws IOException, SAXException, ParserConfigurationException {
        logger.info("start");
        try (DB db = new DB(DB_DIR, Z, CACHE_SIZE)) {
//            Parser.parse(INPUT_DIR, db);
//            Interpolator.interpolate(db);
//            Renderer.render(db, IMAGE_DIR);
            Debug.renderHtml(db, IMAGE_DIR);
        }
        logger.info("end");

    }

}
