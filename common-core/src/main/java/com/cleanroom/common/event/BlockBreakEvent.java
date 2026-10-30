package com.cleanroom.common.event;

import com.cleanroom.common.math.BlockVec3i;

public class BlockBreakEvent {
    private final BlockVec3i pos;
    private final String blockId;
    private final String playerId;
    private final String toolId;
    private final boolean isSneaking;
    private boolean canceled = false;

    public BlockBreakEvent(BlockVec3i pos, String blockId, String playerId, String toolId, boolean isSneaking) {
        this.pos = pos;
        this.blockId = blockId;
        this.playerId = playerId;
        this.toolId = toolId;
        this.isSneaking = isSneaking;
    }

    public BlockVec3i getPos() { return pos; }
    public String getBlockId() { return blockId; }
    public String getPlayerId() { return playerId; }
    public String getToolId() { return toolId; }
    public boolean isSneaking() { return isSneaking; }
    public boolean isCanceled() { return canceled; }
    public void setCanceled(boolean canceled) { this.canceled = canceled; }
}
