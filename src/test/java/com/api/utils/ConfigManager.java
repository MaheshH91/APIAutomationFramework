package com.api.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    private static final Properties prop = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream("src/test/resources/config.properties")) {
            prop.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String get(String key) {
        return prop.getProperty(key);
    }

    // Overload with default
    public static String get(String key, String defaultValue) {
        return prop.getProperty(key, defaultValue);
    }

    public static String getBaseUrl() {
        String env = prop.getProperty("env", "qa");
        return prop.getProperty("base.url." + env);
    }
}
