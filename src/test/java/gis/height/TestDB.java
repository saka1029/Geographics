package gis.height;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.github.saka1029.gis.height.DB;
import com.github.saka1029.gis.height.HeightEnum;

public class TestDB {

    static File dbDir = new File("data/interpolated/db");
    static int z = 15;
    static int cacheSize = 100;
    static DB db;

    @BeforeClass
    public static void init() throws IOException, SAXException, ParserConfigurationException {
        db = new DB(dbDir, z, cacheSize);
    }

    @AfterClass
    public static void done() throws IOException {
        db.close();
    }

    public static double TEST_LON = 139.713478;
    public static double TEST_LAT = 35.703694;
    public static HeightEnum TEST_TYPE = HeightEnum.地表面;
    public static double TEST_HEIGHT = 42.14;

    @Test
    public void 高度計測() {
        int typeHeight = db.get(TEST_LON, TEST_LAT);
        HeightEnum type = HeightEnum.type(typeHeight);
        double height = HeightEnum.height(typeHeight);
        assertEquals(TEST_TYPE, type);
        assertEquals(TEST_HEIGHT, height, 0.1D);
    }

    @Test
    public void 経度からppp() {
        long ppp = db.ppp(TEST_LON);
        assertEquals(7449864L, ppp);
        int pp = (int) ppp / DB.ENTRY_SIZE;
        int p = (int) ppp % DB.ENTRY_SIZE;
        assertEquals(29101, pp);
        assertEquals(8, p);
    }

    @Test
    public void 緯度からqqq() {
        long qqq = db.qqq(TEST_LAT);
        assertEquals(3302605L, qqq);
        int qq = (int) qqq / DB.ENTRY_SIZE;
        int q = (int) qqq % DB.ENTRY_SIZE;
        assertEquals(12900, qq);
        assertEquals(205, q);
    }

    @Test
    public void 最高点() {
        long ppp = db.ppp(TEST_LON);
        long qqq = db.qqq(TEST_LAT);
        double max = -1;
        for (long i = ppp; i < ppp + DB.ENTRY_SIZE; ++i)
            for (long j = qqq; j < qqq + DB.ENTRY_SIZE; ++j) {
                int typeHeight = db.get(i, j);
                HeightEnum type = HeightEnum.type(typeHeight);
                if (type == HeightEnum.地表面) {
                    double height = HeightEnum.height(typeHeight);
                    if (height > max)
                        max = height;
                }
            }
        assertEquals(42.42D, max, 0.1D);
    }

}
