package com.cleanroom.common.event;

import com.cleanroom.common.math.BlockVec3i;

public class PlayerSleepEvent {
    private final String playerId;
    private final String dimension;
    private final BlockVec3i bedPos;
    private final boolean enteringBed;

    public PlayerSleepEvent(String playerId, String dimension, BlockVec3i bedPos, boolean enteringBed) {
        this.playerId = playerId;
        this.dimension = dimension;
        this.bedPos = bedPos;
        this.enteringBed = enteringBed;
    }

    public String getPlayerId() { return playerId; }
    public String getDimension() { return dimension; }
    public BlockVec3i getBedPos() { return bedPos; }
    public boolean isEnteringBed() { return enteringBed; }
}
