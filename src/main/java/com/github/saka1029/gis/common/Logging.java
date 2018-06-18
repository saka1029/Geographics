package com.github.saka1029.gis.common;

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Logging {

    static {
        System.setProperty("java.util.logging.config.file", "logging.properties");
        try {
            LogManager.getLogManager().readConfiguration();
        } catch (SecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Logger logger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }
}
