package com.github.saka1029.gis.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Util {

    public static Document parse(File file)
            throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        return doc;
    }

    public static Document parse(InputStream is)
            throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder();
        Document doc = dBuilder.parse(is);
        return doc;
    }

    public static void visit(Element element, XMLVisitor... visitors) {
        for (XMLVisitor visitor : visitors)
            if (visitor.predicate.test(element)) {
                visitor.visitor.accept(element);
                return;
            }
        NodeList children = element.getChildNodes();
        for (int i = 0, max = children.getLength(); i < max; ++i) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE)
                visit((Element)child, visitors);
        }
    }

    public static boolean endsWith(String name, String suffix) {
        return name.toLowerCase().endsWith(suffix.toLowerCase());
    }

    public static boolean endsWith(File file, String suffix) {
        return file.getName().toLowerCase().endsWith(suffix.toLowerCase());
    }

    public static void copy(File file, File outDir) throws IOException {
        try (InputStream in = new FileInputStream(file);
            OutputStream out = new FileOutputStream(new File(outDir, file.getName()))) {
            in.transferTo(out);
        }

    }
}
