package com.cleanroom.treecapitator;

import com.cleanroom.common.math.BlockVec3i;
import java.util.*;

public class TreecapitatorEngine {
    private final TreecapitatorConfig config;

    public TreecapitatorEngine(TreecapitatorConfig config) {
        this.config = config;
    }

    public TreecapitatorEngine() {
        this(new TreecapitatorConfig());
    }

    public TreecapitatorConfig getConfig() {
        return config;
    }

    public TreeFellResult fellTree(
            TreeWorldView world,
            BlockVec3i rootPos,
            boolean isSneaking,
            String heldToolId,
            int remainingDurability
    ) {
        if (!config.enabled) {
            return TreeFellResult.failure("DISABLED", 0, 0, 0.0, Collections.emptyList());
        }

        if (!world.isLog(rootPos)) {
            return TreeFellResult.failure("NOT_LOG", 0, 0, 0.0, Collections.emptyList());
        }

        // Sneak mode inversion: if player is sneaking and INVERT mode is active, break single log
        if (isSneaking && "INVERT".equalsIgnoreCase(config.sneakMode)) {
            List<String> drops = world.getDrops(rootPos);
            world.setBlockToAir(rootPos);
            world.spawnDrops(rootPos, drops);
            return TreeFellResult.singleLog("SNEAK_SINGLE_LOG", config.exhaustionPerLog, drops);
        }

        // Axe requirement verification
        boolean hasAxe = heldToolId != null && (heldToolId.contains("_axe") || heldToolId.equals("minecraft:axe"));
        if (config.requireAxe && !hasAxe) {
            List<String> drops = world.getDrops(rootPos);
            world.setBlockToAir(rootPos);
            world.spawnDrops(rootPos, drops);
            return TreeFellResult.failure("NO_AXE", 1, 0, 0.0, drops);
        }

        // 3D BFS Traversal for connected trunk and branches
        List<BlockVec3i> discoveredLogs = new ArrayList<>();
        Queue<BlockVec3i> queue = new ArrayDeque<>();
        Set<BlockVec3i> visited = new HashSet<>();

        queue.add(rootPos);
        visited.add(rootPos);
        discoveredLogs.add(rootPos);

        double maxRadiusSq = config.maxRadius * config.maxRadius;

        while (!queue.isEmpty() && discoveredLogs.size() < config.maxLogs) {
            BlockVec3i curr = queue.poll();
            for (BlockVec3i neighbor : curr.getNeighbors26()) {
                if (visited.contains(neighbor)) {
                    continue;
                }
                visited.add(neighbor);

                // Only search upward or horizontally from root
                if (neighbor.y() < rootPos.y()) {
                    continue;
                }

                if (rootPos.distSq(neighbor) > maxRadiusSq) {
                    continue;
                }

                if (world.isLog(neighbor)) {
                    discoveredLogs.add(neighbor);
                    queue.add(neighbor);
                    if (discoveredLogs.size() >= config.maxLogs) {
                        break;
                    }
                }
            }
        }

        // Anti-Cabin Defense: Natural tree verification
        if (config.requireLeaves) {
            int maxY = rootPos.y();
            for (BlockVec3i log : discoveredLogs) {
                if (log.y() > maxY) {
                    maxY = log.y();
                }
            }

            int upperYThreshold = Math.max(rootPos.y(), maxY - 2);
            Set<BlockVec3i> uniqueLeaves = new HashSet<>();

            for (BlockVec3i log : discoveredLogs) {
                if (log.y() >= upperYThreshold) {
                    // Inspect 5x5x5 bounding volume around upper log
                    for (int dx = -2; dx <= 2; dx++) {
                        for (int dy = -2; dy <= 2; dy++) {
                            for (int dz = -2; dz <= 2; dz++) {
                                BlockVec3i leafCandidate = log.offset(dx, dy, dz);
                                if (!uniqueLeaves.contains(leafCandidate) && world.isLeaf(leafCandidate)) {
                                    uniqueLeaves.add(leafCandidate);
                                }
                            }
                        }
                    }
                }
            }

            if (uniqueLeaves.size() < config.minLeavesRatio) {
                // Classified as cabin wall or player build: preserve build!
                List<String> singleDrops = world.getDrops(rootPos);
                world.setBlockToAir(rootPos);
                world.spawnDrops(rootPos, singleDrops);
                return new TreeFellResult(false, 1, 0, 1, config.exhaustionPerLog, "CABIN_PROTECTED", singleDrops);
            }
        }

        // Tool Durability Bounds: Truncate felling if tool would break
        int logsToFellCount = discoveredLogs.size();
        if (remainingDurability > 0 && remainingDurability < logsToFellCount) {
            logsToFellCount = remainingDurability;
        }

        List<BlockVec3i> felledLogPositions = discoveredLogs.subList(0, logsToFellCount);
        List<String> gatheredDrops = new ArrayList<>();

        for (BlockVec3i logPos : felledLogPositions) {
            List<String> drops = world.getDrops(logPos);
            gatheredDrops.addAll(drops);
            world.setBlockToAir(logPos);
        }

        // Secondary Canopy Leaf Decay
        int decayedLeavesCount = 0;
        if (config.destroyLeaves) {
            Set<BlockVec3i> candidateLeaves = new HashSet<>();
            int r = config.leafRadius;
            for (BlockVec3i logPos : felledLogPositions) {
                for (int dx = -r; dx <= r; dx++) {
                    for (int dy = -r; dy <= r; dy++) {
                        for (int dz = -r; dz <= r; dz++) {
                            BlockVec3i leafPos = logPos.offset(dx, dy, dz);
                            if (logPos.distSq(leafPos) <= (r * r) && world.isLeaf(leafPos)) {
                                candidateLeaves.add(leafPos);
                            }
                        }
                    }
                }
            }

            for (BlockVec3i leafPos : candidateLeaves) {
                List<String> leafDrops = world.getDrops(leafPos);
                gatheredDrops.addAll(leafDrops);
                world.setBlockToAir(leafPos);
                decayedLeavesCount++;
            }
        }

        // Vacuum aggregation at tree root
        if (config.gatherDropsAtRoot) {
            world.spawnDrops(rootPos, gatheredDrops);
        }

        int damageDealt = (int) Math.ceil(logsToFellCount * config.toolDamageMultiplier);
        double totalExhaustion = logsToFellCount * config.exhaustionPerLog;

        return new TreeFellResult(
                true,
                logsToFellCount,
                decayedLeavesCount,
                damageDealt,
                totalExhaustion,
                "SUCCESS",
                gatheredDrops
        );
    }
}
