package dev.xhyrom.lanprops.common.manager;

import dev.xhyrom.lanprops.common.accessors.CustomDedicatedServerProperties;
import dev.xhyrom.lanprops.common.accessors.CustomSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class PropertyManager implements CustomDedicatedServerProperties, CustomSettings {
    private static final HashMap<String, Class<?>> keys = new HashMap<>();

    private static final Logger LOGGER = LogManager.getLogger();
    private final Properties serverProperties = new Properties();
    private final File serverPropertiesFile;

    public PropertyManager(File p_i46372_1) {
        this.serverPropertiesFile = p_i46372_1;
        if (p_i46372_1.exists()) {
            FileInputStream fileInputStream = null;

            try {
                fileInputStream = new FileInputStream(p_i46372_1);
                this.serverProperties.load(fileInputStream);
            } catch (Exception exception) {
                LOGGER.warn("Failed to load {}", p_i46372_1, exception);
                this.saveProperties();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException var11) {
                    }
                }

            }
        } else {
            LOGGER.warn("{} does not exist", p_i46372_1);
            this.saveProperties();
        }

    }

    public final boolean lan_properties$hybridMode = this.getBooleanProperty("hybrid-mode", true);

    public @NotNull Properties lan_properties$properties() {
        return this.serverProperties;
    }

    public boolean lan_properties$onlineMode() {
        return this.getBooleanProperty("online-mode", true);
    }

    public boolean lan_properties$preventProxyConnections() {
        return this.getBooleanProperty("prevent-proxy-connections", false);
    }

    public boolean lan_properties$pvp() {
        return this.getBooleanProperty("pvp", true);
    }

    public boolean lan_properties$allowFlight() {
        return this.getBooleanProperty("allow-flight", false);
    }

    public String lan_properties$motd() {
        return this.getStringProperty("motd", "A Minecraft Server");
    }

    public int lan_properties$playerIdleTimeout() {
        return this.getIntProperty("player-idle-timeout", 0);
    }

    public int lan_properties$serverPort() {
        return this.getIntProperty("server-port", 25565);
    }

    public void lan_properties$serverPort(int port) {
        this.serverProperties.put("server-port", String.valueOf(port));
    }

    public boolean lan_properties$hybridMode() {
        return this.getBooleanProperty("hybrid-mode", this.lan_properties$hybridMode);
    }

    public void saveProperties() {
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(this.serverPropertiesFile);
            this.serverProperties.store(fileOutputStream, "Minecraft server properties");
        } catch (Exception exception) {
            LOGGER.warn("Failed to save {}", this.serverPropertiesFile, exception);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException var10) {
                }
            }

        }

    }

    public File getPropertiesFile() {
        return this.serverPropertiesFile;
    }

    public String getStringProperty(String key, String defaultValue) {
        keys.putIfAbsent(key, String.class);
        if (!this.serverProperties.containsKey(key)) {
            this.serverProperties.setProperty(key, defaultValue);
            this.saveProperties();
            return defaultValue;
        }

        return this.serverProperties.getProperty(key, defaultValue);
    }

    public int getIntProperty(String key, int defaultValue) {
        keys.putIfAbsent(key, int.class);
        try {
            return Integer.parseInt(this.getStringProperty(key, "" + defaultValue));
        } catch (Exception var4) {
            this.serverProperties.setProperty(key, "" + defaultValue);
            this.saveProperties();
            return defaultValue;
        }
    }

    public long getLongProperty(String key, long defaultValue) {
        keys.putIfAbsent(key, long.class);
        try {
            return Long.parseLong(this.getStringProperty(key, "" + defaultValue));
        } catch (Exception var5) {
            this.serverProperties.setProperty(key, "" + defaultValue);
            this.saveProperties();
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        keys.putIfAbsent(key, boolean.class);
        try {
            return Boolean.parseBoolean(this.getStringProperty(key, "" + defaultValue));
        } catch (Exception var4) {
            this.serverProperties.setProperty(key, "" + defaultValue);
            this.saveProperties();
            return defaultValue;
        }
    }

    public void setProperty(String key, Object value) {
        this.serverProperties.setProperty(key, "" + value);
    }

    public boolean hasProperty(String key) {
        return this.serverProperties.containsKey(key);
    }

    public void removeProperty(String key) {
        this.serverProperties.remove(key);
    }

    @Override
    public @NotNull HashMap<String, Class<?>> lan_properties$keys() {
        return keys;
    }
}

