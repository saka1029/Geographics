package com.github.saka1029.gis.height;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.github.saka1029.gis.common.Logging;
import com.github.saka1029.gis.common.Util;
import com.github.saka1029.gis.common.XMLVisitor;

/**
 * 国土地理院の基盤地図情報のうち、数値標高データを読み込んで
 * ローカルデータベースに格納します。
 */
public class Parser {

    static Logger logger = Logging.logger(Parser.class);

    /**
     * 数値標高データのXMLファイルを読み込んだ結果を格納します。
     */
    static class HeightData {
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

    /**
     * 数値標高データの中のgml:tupleListタグのテキストの内容を
     * HeightDataEntryの配列に変換します。
     */
    static int[] tuples(String text) {
        String[] entries = text.trim().split("\\s+");
        int[] tuples = new int[entries.length];
        for (int i = 0, max = entries.length; i < max; ++i) {
            String[] tuple = entries[i].split(",");
            HeightEnum type = HeightEnum.valueOf(tuple[0]);
            double height = Double.parseDouble(tuple[1]);
            tuples[i] = type.typeHeight(height);
        }
        return tuples;
    }

    /**
     * 基盤地図情報（数値標高データ）のDOMをHeightDataに変換します。
     */
    static HeightData heightData(Document doc) {
        HeightData data = new HeightData();
        Util.visit(doc.getDocumentElement(),
            XMLVisitor.of("gml:lowerCorner", e -> {
                String[] t = e.getTextContent().trim().split(" ");
                data.minLat = Double.parseDouble(t[0]);
                data.minLon = Double.parseDouble(t[1]);
            }),
            XMLVisitor.of("gml:upperCorner", e -> {
                String[] t = e.getTextContent().trim().split(" ");
                data.maxLat = Double.parseDouble(t[0]);
                data.maxLon = Double.parseDouble(t[1]);
            }),
            XMLVisitor.of("gml:high", e -> {
                String[] t = e.getTextContent().trim().split(" ");
                data.divX = Integer.parseInt(t[0]);
                data.divY = Integer.parseInt(t[1]);
            }),
            XMLVisitor.of("gml:startPoint", e -> {
                String[] t = e.getTextContent().trim().split(" ");
                data.startX = Integer.parseInt(t[0]);
                data.startY = Integer.parseInt(t[1]);
            }),
            XMLVisitor.of("gml:tupleList", e -> {
                data.tuples = tuples(e.getTextContent());
            }));
        return data;
    }

    static void write(int x, int y, double lon, double lat, int typeHeight, DB db) throws IOException {
        long ppp = db.ppp(lon);
        long qqq = db.qqq(lat);
        db.put(ppp, qqq, typeHeight);
    }

    static void write(HeightData data, DB db) throws IOException {
        if (data.tuples == null) return;
        double unitLat = (data.maxLat - data.minLat) / data.divY;
        double unitLon = (data.maxLon - data.minLon) / data.divX;
        int i = 0;
        int startX = data.startX;
        int startY = data.startY;
        L: for (int y = startY; y <= data.divY; ++y) {
            double lat = data.maxLat - unitLat * y;
            for (int x = startX; x <= data.divX; ++x, ++i) {
                if (i >= data.tuples.length)
                    break L;
                double lon = data.minLon + unitLon * x;
                int tuple = data.tuples[i];
                write(x, y, lon, lat, tuple, db);
            }
            startX = 0;
        }
    }

    static void parseFiles(File inFile, DB db)
            throws SAXException, IOException, ParserConfigurationException {
        if (inFile.isDirectory()) {
            for (File child : inFile.listFiles())
                parseFiles(child, db);
        } else if (Util.endsWith(inFile, ".xml")) {
            logger.info(inFile.getName());
            write(heightData(Util.parse(inFile)), db);
        } else if (Util.endsWith(inFile, ".zip")) {
            try (ZipFile zip = new ZipFile(inFile)) {
                for (Enumeration<? extends ZipEntry> en = zip.entries(); en.hasMoreElements(); ) {
                    ZipEntry e = en.nextElement();
                    logger.info(e.getName());
                    if (!e.getName().toLowerCase().endsWith(".xml"))
                        continue;
                    try (InputStream is = zip.getInputStream(e)) {
                        write(heightData(Util.parse(is)), db);
                    }
                }
            }
        }
    }

    public static void parse(File inFile, DB db)
            throws SAXException, IOException, ParserConfigurationException {
        logger.info("parse start");
        parseFiles(inFile, db);
        logger.info("parse end");
    }

}
