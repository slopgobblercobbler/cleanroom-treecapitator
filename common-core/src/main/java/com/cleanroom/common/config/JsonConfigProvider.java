package com.cleanroom.common.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class JsonConfigProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private JsonConfigProvider() {}

    public static <T> T loadConfig(Path configFile, Class<T> configClass, T defaultConfig) {
        if (!Files.exists(configFile)) {
            saveConfig(configFile, defaultConfig);
            return defaultConfig;
        }

        try (Reader reader = Files.newBufferedReader(configFile)) {
            T loaded = GSON.fromJson(reader, configClass);
            return loaded != null ? loaded : defaultConfig;
        } catch (Exception e) {
            System.err.println("[CleanRoom] Failed to read config from " + configFile + ": " + e.getMessage());
            return defaultConfig;
        }
    }

    public static <T> boolean saveConfig(Path configFile, T config) {
        try {
            if (configFile.getParent() != null) {
                Files.createDirectories(configFile.getParent());
            }
            try (Writer writer = Files.newBufferedWriter(configFile)) {
                GSON.toJson(config, writer);
                return true;
            }
        } catch (IOException e) {
            System.err.println("[CleanRoom] Failed to write config to " + configFile + ": " + e.getMessage());
            return false;
        }
    }
}
