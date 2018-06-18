package com.github.saka1029.gis.common;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.w3c.dom.Element;

public class XMLVisitor {

    public final Predicate<Element> predicate;
    public final Consumer<Element> visitor;

    XMLVisitor(Predicate<Element> predicate, Consumer<Element> visitor) {
        this.predicate = predicate;
        this.visitor = visitor;
    }

    public static XMLVisitor of(Predicate<Element> predicate, Consumer<Element> visitor) {
        return new XMLVisitor(predicate, visitor);
    }

    public static XMLVisitor of(String tagName, Consumer<Element> visitor) {
        return new XMLVisitor(e -> e.getTagName().equals(tagName), visitor);
    }

}
