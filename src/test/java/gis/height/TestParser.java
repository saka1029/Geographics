package gis.height;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.github.saka1029.gis.height.DB;
import com.github.saka1029.gis.height.Debug;
import com.github.saka1029.gis.height.Parser;

public class TestParser {

    static final File IN_FILE = new File("data/input");
    static final File PARSED_DIR = new File("data/parsed");
    static final File DB_DIR = new File(PARSED_DIR, "db");
    static final File DUMP_DIR = new File(PARSED_DIR, "dump");
    static final int Z = 15;
    static final int CACHE_SIZE = 100;

    @Test
    public void testParse() throws IOException, SAXException, ParserConfigurationException {
        if (!DB_DIR.exists()) DB_DIR.mkdirs();
        if (!DUMP_DIR.exists()) DUMP_DIR.mkdirs();
        try (DB db = new DB(DB_DIR, Z, CACHE_SIZE)) {
            Parser.parse(IN_FILE, db);
            Debug.dump(db, DUMP_DIR);
            Debug.renderHtml(db, DUMP_DIR);
        }
    }

}
