package com.cleanroom.common.api;

import java.nio.file.Path;

public interface PlatformAdapter {
    PlatformType getPlatformType();

    EnvironmentSide getEnvironmentSide();

    boolean isModLoaded(String modId);

    Path getConfigDirectory();

    void registerEventListener(Object listener);
}
