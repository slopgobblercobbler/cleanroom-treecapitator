package com.cleanroom.common.api;

public enum PlatformType {
    FABRIC("fabric"),
    FORGE("forge"),
    NEOFORGE("neoforge"),
    STANDALONE("standalone");

    private final String id;

    PlatformType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
