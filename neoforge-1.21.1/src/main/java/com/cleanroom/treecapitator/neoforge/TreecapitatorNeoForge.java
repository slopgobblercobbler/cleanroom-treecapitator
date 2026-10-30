package com.cleanroom.treecapitator.neoforge;

import com.cleanroom.treecapitator.TreecapitatorCore;

public class TreecapitatorNeoForge {
    private final TreecapitatorCore core = new TreecapitatorCore();

    public TreecapitatorNeoForge() {
        System.out.println("[CleanRoom NeoForge] Bootstrapping " + TreecapitatorCore.MOD_NAME);
        core.onInitializeCommon();
    }
}
