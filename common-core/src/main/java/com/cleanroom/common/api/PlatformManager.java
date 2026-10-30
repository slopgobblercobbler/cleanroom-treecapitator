package com.cleanroom.common.api;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class PlatformManager {
    private static PlatformAdapter instance = new DefaultPlatformAdapter();

    private PlatformManager() {}

    public static PlatformAdapter getPlatform() {
        return instance;
    }

    public static void setPlatform(PlatformAdapter platform) {
        if (platform != null) {
            instance = platform;
        }
    }

    private static class DefaultPlatformAdapter implements PlatformAdapter {
        @Override
        public PlatformType getPlatformType() {
            return PlatformType.STANDALONE;
        }

        @Override
        public EnvironmentSide getEnvironmentSide() {
            return EnvironmentSide.SERVER;
        }

        @Override
        public boolean isModLoaded(String modId) {
            return false;
        }

        @Override
        public Path getConfigDirectory() {
            return Paths.get("config");
        }

        @Override
        public void registerEventListener(Object listener) {}
    }
}
