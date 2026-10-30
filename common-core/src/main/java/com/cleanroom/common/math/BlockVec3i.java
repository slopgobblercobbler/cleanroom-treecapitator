package com.cleanroom.common.math;

import java.util.ArrayList;
import java.util.List;

public record BlockVec3i(int x, int y, int z) {
    public static final BlockVec3i ZERO = new BlockVec3i(0, 0, 0);

    public BlockVec3i offset(int dx, int dy, int dz) {
        return new BlockVec3i(this.x + dx, this.y + dy, this.z + dz);
    }

    public BlockVec3i above() {
        return offset(0, 1, 0);
    }

    public BlockVec3i below() {
        return offset(0, -1, 0);
    }

    public BlockVec3i north() {
        return offset(0, 0, -1);
    }

    public BlockVec3i south() {
        return offset(0, 0, 1);
    }

    public BlockVec3i west() {
        return offset(-1, 0, 0);
    }

    public BlockVec3i east() {
        return offset(1, 0, 0);
    }

    public double distSq(BlockVec3i other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        return dx * dx + dy * dy + dz * dz;
    }

    public double dist(BlockVec3i other) {
        return Math.sqrt(distSq(other));
    }

    public int manhattanDist(BlockVec3i other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y) + Math.abs(this.z - other.z);
    }

    public List<BlockVec3i> getNeighbors6() {
        List<BlockVec3i> neighbors = new ArrayList<>(6);
        neighbors.add(above());
        neighbors.add(below());
        neighbors.add(north());
        neighbors.add(south());
        neighbors.add(west());
        neighbors.add(east());
        return neighbors;
    }

    public List<BlockVec3i> getNeighbors26() {
        List<BlockVec3i> neighbors = new ArrayList<>(26);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    neighbors.add(new BlockVec3i(this.x + dx, this.y + dy, this.z + dz));
                }
            }
        }
        return neighbors;
    }
}
