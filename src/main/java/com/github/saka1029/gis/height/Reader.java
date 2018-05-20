package com.github.saka1029.gis.height;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.github.saka1029.gis.xml.Util;
import com.github.saka1029.gis.xml.Visitor;

/**
 * 国土地理院の基盤地図情報のうち、数値標高データを読み込んで
 * ローカルデータベースに格納します。
 */
public class Reader {

    /**
     * 数値標高データの各点の情報を格納します。
     */
    static class HeightDataEntry {
        /** 標高の種類です */
        HeightType type;
        /** メートル単位の標高です */
        double height;
    }

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
        HeightDataEntry[] tuples;
    }

    /**
     * 数値標高データの中のgml:tupleListタグのテキストの内容を
     * HeightDataEntryの配列に変換します。
     */
    static HeightDataEntry[] heightDataEntries(String text) {
        String[] entries = text.trim().split("\\s+");
        HeightDataEntry[] tuples = new HeightDataEntry[entries.length];
        for (int i = 0, max = entries.length; i < max; ++i) {
            String[] tuple = entries[i].split(",");
            HeightDataEntry entry = new HeightDataEntry();
            entry.type = HeightType.valueOf(tuple[0]);
            entry.height = Double.parseDouble(tuple[1]);
            tuples[i] = entry;
        }
        return tuples;
    }

    /**
     * 基盤地図情報（数値標高データ）のDOMをHeightDataに変換します。
     */
    static HeightData heightData(Document doc) {
        HeightData data = new HeightData();
        Util.visit(doc.getDocumentElement(),
            Visitor.of("gml:lowerCorner", e -> {
                String[] t = e.getTextContent().trim().split(" ");
                data.minLat = Double.parseDouble(t[0]);
                data.minLon = Double.parseDouble(t[1]);
            }),
            Visitor.of("gml:upperCorner", e -> {
                String[] t = e.getTextContent().trim().split(" ");
                data.maxLat = Double.parseDouble(t[0]);
                data.maxLon = Double.parseDouble(t[1]);
            }),
            Visitor.of("gml:high", e -> {
                String[] t = e.getTextContent().trim().split(" ");
                data.divX = Integer.parseInt(t[0]);
                data.divY = Integer.parseInt(t[1]);
            }),
            Visitor.of("gml:startPoint", e -> {
                String[] t = e.getTextContent().trim().split(" ");
                data.startX = Integer.parseInt(t[0]);
                data.startY = Integer.parseInt(t[1]);
            }),
            Visitor.of("gml:tupleList", e -> {
                data.tuples = heightDataEntries(e.getTextContent());
            }));
        return data;
    }

    public static void main(String[] args)
            throws SAXException, IOException, ParserConfigurationException {
        File xml = new File("data/FG-GML-5339-45-00-DEM5A-20161001.xml");
        Document doc = Util.parse(xml);
        HeightData data = heightData(doc);
        System.out.println(data);
    }

}
