package com.github.saka1029.gis.xml;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.w3c.dom.Element;

public class Visitor {

    public final Predicate<Element> predicate;
    public final Consumer<Element> visitor;

    public Visitor(Predicate<Element> predicate, Consumer<Element> visitor) {
        this.predicate = predicate;
        this.visitor = visitor;
    }

    public static Visitor of(Predicate<Element> predicate, Consumer<Element> visitor) {
        return new Visitor(predicate, visitor);
    }

    public static Visitor of(String tagName, Consumer<Element> visitor) {
        return new Visitor(e -> e.getTagName().equals(tagName), visitor);
    }

}
