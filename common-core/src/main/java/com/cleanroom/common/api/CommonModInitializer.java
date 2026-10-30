package com.cleanroom.common.api;

public interface CommonModInitializer {
    String getModId();

    void onInitializeCommon();

    default void onClientSetup() {}

    default void onServerSetup() {}
}
