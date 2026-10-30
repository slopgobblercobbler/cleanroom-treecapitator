package com.cleanroom.common.model;

import com.cleanroom.common.math.Vec3d;

public record BlockBox(Vec3d min, Vec3d max) {
    public static BlockBox fromPoints(Vec3d a, Vec3d b) {
        double minX = Math.min(a.x(), b.x());
        double minY = Math.min(a.y(), b.y());
        double minZ = Math.min(a.z(), b.z());
        double maxX = Math.max(a.x(), b.x());
        double maxY = Math.max(a.y(), b.y());
        double maxZ = Math.max(a.z(), b.z());
        return new BlockBox(new Vec3d(minX, minY, minZ), new Vec3d(maxX, maxY, maxZ));
    }

    public boolean contains(Vec3d point) {
        return point.x() >= min.x() && point.x() <= max.x() &&
               point.y() >= min.y() && point.y() <= max.y() &&
               point.z() >= min.z() && point.z() <= max.z();
    }

    public boolean intersects(BlockBox other) {
        return this.max.x() >= other.min.x() && this.min.x() <= other.max.x() &&
               this.max.y() >= other.min.y() && this.min.y() <= other.max.y() &&
               this.max.z() >= other.min.z() && this.min.z() <= other.max.z();
    }

    public BlockBox expand(double amount) {
        return new BlockBox(
                new Vec3d(min.x() - amount, min.y() - amount, min.z() - amount),
                new Vec3d(max.x() + amount, max.y() + amount, max.z() + amount)
        );
    }
}
