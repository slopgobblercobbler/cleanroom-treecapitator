package com.cleanroom.treecapitator.fabric;

import com.cleanroom.treecapitator.TreecapitatorCore;

public class TreecapitatorFabric {
    private final TreecapitatorCore core = new TreecapitatorCore();

    public void onInitialize() {
        System.out.println("[CleanRoom Fabric] Bootstrapping " + TreecapitatorCore.MOD_NAME);
        core.onInitializeCommon();
    }
}
