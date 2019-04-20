package gis.height;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.github.saka1029.gis.height.HeightEnum;

public class TestSax {

    @Test
    public void test() throws ParserConfigurationException, SAXException, IOException {
        File in = new File("data/input/FG-GML-5339-45-68-DEM5A-20170202.xml");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        Handler handler = new Handler();
        parser.parse(in, handler);
        System.out.println(Arrays.toString(handler.data.tuples));
    }

}

class HeightData {
    int divY;
    int divX;
    double minLon;
    double minLat;
    double maxLon;
    double maxLat;
    int startX;
    int startY;
    int[] tuples;
}

class Handler extends DefaultHandler {

    static final Map<String, BiConsumer<String, HeightData>> map = Map.of(
        "gml:lowerCorner", (t, d) -> {
            String[] x = t.trim().split(" ");
            d.minLat = Double.parseDouble(x[0]);
            d.minLon = Double.parseDouble(x[1]);
        },
        "gml:upperCorner", (t, d) -> {
            String[] x = t.trim().split(" ");
            d.maxLat = Double.parseDouble(x[0]);
            d.maxLon = Double.parseDouble(x[1]);
        },
        "gml:heigh", (t, d) -> {
            String[] x = t.trim().split(" ");
            d.divX = Integer.parseInt(x[0]);
            d.divY = Integer.parseInt(x[1]);
        },
        "gml:startPoint", (t, d) -> {
            String[] x = t.trim().split(" ");
            d.startX = Integer.parseInt(x[0]);
            d.startY = Integer.parseInt(x[1]);
        },
        "gml:tupleList", (t, d) -> {
            String[] x = t.trim().split("\\s+");
            d.tuples = new int[x.length];
            for (int i = 0, max = x.length; i < max; ++i) {
                String[] tuple = x[i].split(",");
                HeightEnum type = HeightEnum.valueOf(tuple[0]);
                double height = Double.parseDouble(tuple[1]);
                d.tuples[i] = type.typeHeight(height);
            }
        });

    final HeightData data = new HeightData();
    final StringBuilder sb = new StringBuilder();
    boolean saveText = false;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (map.containsKey(qName)) {
            sb.setLength(0);
            saveText = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (map.containsKey(qName)) {
            map.get(qName).accept(sb.toString(), data);
            saveText = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (saveText)
            sb.append(ch, start, length);
    }
}
