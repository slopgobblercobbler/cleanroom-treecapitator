package com.cleanroom.treecapitator;

import com.cleanroom.common.api.CommonModInitializer;
import com.cleanroom.common.event.BlockBreakEvent;
import com.cleanroom.common.event.CommonEventBus;
import com.cleanroom.common.telemetry.CleanRoomTelemetry;

import java.util.Collections;
import java.util.List;

public class TreecapitatorCore implements CommonModInitializer {
    public static final String MOD_ID = "cleanroom_treecapitator";
    public static final String MOD_NAME = "Treecapitator CleanRoom";

    private static TreecapitatorCore instance;
    private final TreecapitatorConfig config = new TreecapitatorConfig();
    private final TreecapitatorEngine engine = new TreecapitatorEngine(config);

    public static TreecapitatorCore getInstance() {
        return instance;
    }

    public TreecapitatorConfig getConfig() {
        return config;
    }

    public TreecapitatorEngine getEngine() {
        return engine;
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }

    @Override
    public void onInitializeCommon() {
        instance = this;
        System.out.println("[CleanRoom] Initialized core logic for " + MOD_NAME + " (" + MOD_ID + ")");
        CleanRoomTelemetry.record("INIT", "Treecapitator initialized with maxLogs=" + config.maxLogs);

        // Register block break listener on CommonEventBus
        CommonEventBus.getInstance().register(BlockBreakEvent.class, event -> {
            if (!config.enabled) return;
            if (event.getBlockId() != null && event.getBlockId().contains("_log")) {
                CleanRoomTelemetry.record("TREECAPITATOR_INTERCEPT", "Intercepted log break at " + event.getPos());
            }
        });
    }
}
