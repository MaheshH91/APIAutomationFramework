package com.api.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import com.api.constants.FrameworkConstants;

public final class ConfigManager {
    private static final Properties prop = new Properties();

    private ConfigManager() {}

    static {
        try (FileInputStream fis = new FileInputStream(FrameworkConstants.CONFIG_FILE_PATH)) {
            prop.load(fis);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load configuration file at: " + FrameworkConstants.CONFIG_FILE_PATH + " -> " + e.getMessage());
        }
    }

    public static String get(String key) {
        return prop.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return prop.getProperty(key, defaultValue);
    }

    public static String getBaseUrl() {
        String env = prop.getProperty("env", "qa");
        return prop.getProperty("base.url." + env);
    }
}