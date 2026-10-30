package com.cleanroom.treecapitator;

import com.cleanroom.common.config.JsonConfigProvider;
import java.nio.file.Path;

public class TreecapitatorConfig {
    public boolean enabled = true;
    public boolean requireAxe = true;
    public String sneakMode = "INVERT"; // "INVERT": sneak cuts single log; "NORMAL": sneak fells tree
    public int maxLogs = 256;
    public double maxRadius = 32.0;
    public boolean destroyLeaves = true;
    public int leafRadius = 4;
    public double toolDamageMultiplier = 1.0;
    public boolean gatherDropsAtRoot = true;
    public boolean requireLeaves = true;
    public int minLeavesRatio = 3; // Minimum leaves in upper canopy for natural tree verification
    public double exhaustionPerLog = 0.005;

    public static TreecapitatorConfig load(Path configPath) {
        return JsonConfigProvider.loadConfig(configPath, TreecapitatorConfig.class, new TreecapitatorConfig());
    }

    public boolean save(Path configPath) {
        return JsonConfigProvider.saveConfig(configPath, this);
    }
}
