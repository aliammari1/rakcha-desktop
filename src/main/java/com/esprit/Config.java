package com.esprit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.yaml.snakeyaml.Yaml;

/**
 * Config class for the RAKCHA JavaFX desktop application.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class Config {
    private static final Logger LOGGER = Logger.getLogger(Config.class.getName());
    private static final String CONFIG_PATH = "src/main/java/com/esprit/config.yml";
    private static Config instance;
    private final Map<String, Object> config;

    private Config() {
        this.config = loadConfig();
    }

    /**
     * Retrieves the Instance value.
     *
     * @return the Instance value
     */
    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    /**
     * Retrieves the value.
     *
     * @return the value
     */
    public String get(String key) {
        Object value = config.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Retrieves the OrDefault value.
     *
     * @return the OrDefault value
     */
    public String getOrDefault(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Sets the value.
     *
     * @param set
     *            the value to set
     */
    public void set(String key, String value) {
        config.put(key, value);
        saveConfig();
    }

    private Map<String, Object> loadConfig() {
        File configFile = new File(CONFIG_PATH);
        if (!configFile.exists()) {
            LOGGER.info("Config file not found, creating new one");
            return createDefaultConfig();
        }

        try (FileInputStream input = new FileInputStream(configFile)) {
            Yaml yaml = new Yaml();
            Object loaded = yaml.load(input);
            if (loaded instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> result = (Map<String, Object>) loaded;
                return result;
            }
            return new HashMap<>();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading config file", e);
            return new HashMap<>();
        }
    }

    private Map<String, Object> createDefaultConfig() {
        Map<String, Object> defaultConfig = new HashMap<>();
        // Database configuration
        defaultConfig.put("db.url", System.getenv("DB_URL"));
        defaultConfig.put("db.user", System.getenv("DB_USER"));
        defaultConfig.put("db.password", System.getenv("DB_PASSWORD"));

        // API Keys
        defaultConfig.put("vonage.api.key", System.getenv("VONAGE_API_KEY"));
        defaultConfig.put("vonage.api.secret", System.getenv("VONAGE_API_SECRET"));
        defaultConfig.put("stripe.api.key", System.getenv("STRIPE_API_KEY"));
        defaultConfig.put("openai.api.key", System.getenv("OPENAI_API_KEY"));
        defaultConfig.put("openai.model", System.getenv("OPENAI_MODEL"));
        defaultConfig.put("youtube.api.key", System.getenv("YOUTUBE_API_KEY"));
        defaultConfig.put("microsoft.client.id", System.getenv("MICROSOFT_CLIENT_ID"));
        defaultConfig.put("microsoft.client.secret", System.getenv("MICROSOFT_CLIENT_SECRET"));
        defaultConfig.put("google.client.id", System.getenv("GOOGLE_CLIENT_ID"));
        defaultConfig.put("google.client.secret", System.getenv("GOOGLE_CLIENT_SECRET"));

        // Email configuration
        defaultConfig.put("email.from", System.getenv("EMAIL_FROM"));
        defaultConfig.put("email.username", System.getenv("EMAIL_USERNAME"));
        defaultConfig.put("email.password", System.getenv("JAVAMAIL_APP_PASSWORD"));

        saveConfig(defaultConfig);
        return defaultConfig;
    }

    private void saveConfig() {
        saveConfig(this.config);
    }

    private void saveConfig(Map<String, Object> configToSave) {
        try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
            Yaml yaml = new Yaml();
            yaml.dump(configToSave, writer);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving config file", e);
        }
    }
}
