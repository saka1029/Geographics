package com.github.saka1029.gis.xml;

import java.io.File;
import java.io.IOException;

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

    public static void visit(Element element, Visitor... visitors) {
        for (Visitor visitor : visitors)
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

}
