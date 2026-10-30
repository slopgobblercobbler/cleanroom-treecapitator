package com.cleanroom.common.math;

public record Vec3d(double x, double y, double z) {
    public static final Vec3d ZERO = new Vec3d(0.0, 0.0, 0.0);

    public Vec3d add(double dx, double dy, double dz) {
        return new Vec3d(this.x + dx, this.y + dy, this.z + dz);
    }

    public Vec3d add(Vec3d other) {
        return add(other.x, other.y, other.z);
    }

    public Vec3d subtract(Vec3d other) {
        return add(-other.x, -other.y, -other.z);
    }

    public Vec3d scale(double factor) {
        return new Vec3d(this.x * factor, this.y * factor, this.z * factor);
    }

    public double dot(Vec3d other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public double lengthSq() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public double length() {
        return Math.sqrt(lengthSq());
    }

    public Vec3d normalize() {
        double len = length();
        if (len < 1.0E-5) {
            return ZERO;
        }
        return scale(1.0 / len);
    }

    public double distanceTo(Vec3d other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double distanceSq(Vec3d other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        return dx * dx + dy * dy + dz * dz;
    }
}
