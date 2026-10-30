package com.cleanroom.common.math;

public final class RaycastMath {
    private RaycastMath() {}

    public record RayResult(boolean hit, double distance, Vec3d hitPoint) {
        public static final RayResult MISS = new RayResult(false, Double.POSITIVE_INFINITY, Vec3d.ZERO);
    }

    /**
     * Clean-room ray-AABB intersection test using the slab method.
     */
    public static RayResult intersectAABB(Vec3d origin, Vec3d direction, Vec3d boxMin, Vec3d boxMax) {
        Vec3d dir = direction.normalize();

        double tMin = 0.0;
        double tMax = Double.POSITIVE_INFINITY;

        // X axis
        if (Math.abs(dir.x()) < 1.0E-8) {
            if (origin.x() < boxMin.x() || origin.x() > boxMax.x()) return RayResult.MISS;
        } else {
            double invD = 1.0 / dir.x();
            double t1 = (boxMin.x() - origin.x()) * invD;
            double t2 = (boxMax.x() - origin.x()) * invD;
            if (t1 > t2) { double tmp = t1; t1 = t2; t2 = tmp; }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) return RayResult.MISS;
        }

        // Y axis
        if (Math.abs(dir.y()) < 1.0E-8) {
            if (origin.y() < boxMin.y() || origin.y() > boxMax.y()) return RayResult.MISS;
        } else {
            double invD = 1.0 / dir.y();
            double t1 = (boxMin.y() - origin.y()) * invD;
            double t2 = (boxMax.y() - origin.y()) * invD;
            if (t1 > t2) { double tmp = t1; t1 = t2; t2 = tmp; }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) return RayResult.MISS;
        }

        // Z axis
        if (Math.abs(dir.z()) < 1.0E-8) {
            if (origin.z() < boxMin.z() || origin.z() > boxMax.z()) return RayResult.MISS;
        } else {
            double invD = 1.0 / dir.z();
            double t1 = (boxMin.z() - origin.z()) * invD;
            double t2 = (boxMax.z() - origin.z()) * invD;
            if (t1 > t2) { double tmp = t1; t1 = t2; t2 = tmp; }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) return RayResult.MISS;
        }

        Vec3d hitPoint = origin.add(dir.scale(tMin));
        return new RayResult(true, tMin, hitPoint);
    }
}
