package com.cleanroom.common.math;

import java.util.*;
import java.util.function.Predicate;

public final class SpatialBfs {
    private SpatialBfs() {}

    /**
     * Traverses connected blocks in 26-directions matching the predicate.
     *
     * @param origin Starting block position
     * @param matcher Predicate testing whether candidate position is part of connected cluster
     * @param maxBlocks Maximum total blocks to traverse
     * @param maxRadius Maximum Euclidean distance from origin
     * @return List of matching positions discovered in traversal order
     */
    public static List<BlockVec3i> findConnected(
            BlockVec3i origin,
            Predicate<BlockVec3i> matcher,
            int maxBlocks,
            double maxRadius
    ) {
        List<BlockVec3i> results = new ArrayList<>();
        if (!matcher.test(origin)) {
            return results;
        }

        Queue<BlockVec3i> queue = new ArrayDeque<>();
        Set<BlockVec3i> visited = new HashSet<>();

        queue.add(origin);
        visited.add(origin);
        results.add(origin);

        double maxRadiusSq = maxRadius * maxRadius;

        while (!queue.isEmpty() && results.size() < maxBlocks) {
            BlockVec3i current = queue.poll();

            for (BlockVec3i neighbor : current.getNeighbors26()) {
                if (visited.contains(neighbor)) {
                    continue;
                }
                visited.add(neighbor);

                if (origin.distSq(neighbor) > maxRadiusSq) {
                    continue;
                }

                if (matcher.test(neighbor)) {
                    results.add(neighbor);
                    queue.add(neighbor);
                    if (results.size() >= maxBlocks) {
                        break;
                    }
                }
            }
        }

        return results;
    }
}
