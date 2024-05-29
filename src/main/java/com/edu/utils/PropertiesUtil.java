package com.edu.utils;

import java.io.*;
import java.util.Properties;

public class PropertiesUtil {
   private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private PropertiesUtil() {}

    public static String get(String key) {
        return properties.getProperty(key);
    }

    private static void loadProperties() {

        try(InputStream path = PropertiesUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(path);
        } catch (IOException e) {
           e.printStackTrace();
        }
    }
}
