package com.api.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ConfigManager {

    private static final Logger log = LogManager.getLogger(ConfigManager.class);
    private static final Properties prop = new Properties();

    private ConfigManager() {}

    static {
        loadProperties();
    }

    private static void loadProperties() {
        // 1. Try loading from classpath first (works reliably across Maven/Surefire forks)
        try (InputStream is = ConfigManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                prop.load(is);
                log.info("Successfully loaded config.properties from classpath.");
                return;
            }
        } catch (Exception e) {
            log.warn("Could not load config.properties from classpath: {}", e.getMessage());
        }

        // 2. Fallback: try loading from file system path
        String[] fallbackPaths = {
            "src/test/resources/config.properties",
            "src/main/resources/config.properties",
            "config.properties"
        };

        for (String path : fallbackPaths) {
            File file = new File(path);
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    prop.load(fis);
                    log.info("Successfully loaded config.properties from: {}", path);
                    return;
                } catch (Exception e) {
                    log.warn("Failed reading from {}: {}", path, e.getMessage());
                }
            }
        }

        log.error("Unable to locate config.properties anywhere in classpath or local paths.");
    }

    public static String get(String key) {
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isBlank()) {
            return sysProp.trim();
        }
        return prop.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        String val = get(key);
        return (val == null || val.isBlank()) ? defaultValue : val;
    }

    public static String getProperty(String key) {
        return get(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return get(key, defaultValue);
    }

    public static String getBaseUrl() {
        String directUrl = get("base.url");
        if (directUrl != null && !directUrl.isBlank()) {
            return directUrl;
        }

        String env = get("env", "qa").toLowerCase();
        String envUrl = get("base.url." + env);
        if (envUrl != null && !envUrl.isBlank()) {
            return envUrl;
        }

        return "http://localhost:8089";
    }
}