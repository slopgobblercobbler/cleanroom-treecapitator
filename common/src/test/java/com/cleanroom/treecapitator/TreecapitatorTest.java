package com.cleanroom.treecapitator;

import com.cleanroom.common.math.BlockVec3i;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TreecapitatorTest {

    private static class MockTreeWorld implements TreeWorldView {
        private final Map<BlockVec3i, String> blocks = new HashMap<>();
        private final List<String> spawnedDrops = new ArrayList<>();

        public void setBlock(BlockVec3i pos, String blockId) {
            blocks.put(pos, blockId);
        }

        @Override
        public String getBlockId(BlockVec3i pos) {
            return blocks.getOrDefault(pos, "minecraft:air");
        }

        @Override
        public boolean isLog(BlockVec3i pos) {
            String id = getBlockId(pos);
            return id.endsWith("_log");
        }

        @Override
        public boolean isLeaf(BlockVec3i pos) {
            String id = getBlockId(pos);
            return id.endsWith("_leaves");
        }

        @Override
        public boolean setBlockToAir(BlockVec3i pos) {
            return blocks.remove(pos) != null;
        }

        @Override
        public List<String> getDrops(BlockVec3i pos) {
            String id = getBlockId(pos);
            if ("minecraft:air".equals(id)) return Collections.emptyList();
            return List.of(id);
        }

        @Override
        public void spawnDrops(BlockVec3i pos, List<String> drops) {
            spawnedDrops.addAll(drops);
        }
    }

    private MockTreeWorld world;
    private TreecapitatorEngine engine;

    @BeforeEach
    void setUp() {
        world = new MockTreeWorld();
        engine = new TreecapitatorEngine();
    }

    private BlockVec3i buildNaturalTree(int rootY, int logCount) {
        BlockVec3i root = new BlockVec3i(0, rootY, 0);
        for (int y = rootY; y < rootY + logCount; y++) {
            world.setBlock(new BlockVec3i(0, y, 0), "minecraft:oak_log");
        }
        int topY = rootY + logCount - 1;
        // Place canopy leaves
        world.setBlock(new BlockVec3i(1, topY, 0), "minecraft:oak_leaves");
        world.setBlock(new BlockVec3i(-1, topY, 0), "minecraft:oak_leaves");
        world.setBlock(new BlockVec3i(0, topY, 1), "minecraft:oak_leaves");
        world.setBlock(new BlockVec3i(0, topY, -1), "minecraft:oak_leaves");
        return root;
    }

    @Test
    void testFellNaturalTreeSuccess() {
        BlockVec3i root = buildNaturalTree(64, 5);
        TreeFellResult res = engine.fellTree(world, root, false, "minecraft:diamond_axe", 1561);

        assertTrue(res.success());
        assertEquals(5, res.felledLogs());
        assertEquals(5, res.damageDealt());
        assertEquals(0.025, res.exhaustionAdded(), 1e-4);
        assertEquals("SUCCESS", res.reason());
        assertTrue(res.decayedLeaves() > 0);

        // Verify all 5 trunk logs in world are removed
        for (int y = 64; y < 69; y++) {
            assertEquals("minecraft:air", world.getBlockId(new BlockVec3i(0, y, 0)));
        }
        assertTrue(world.spawnedDrops.size() >= 5);
    }

    @Test
    void testAntiCabinDefensePreservesCabinWall() {
        // Player cabin wall: 5 logs with ZERO leaves
        BlockVec3i root = new BlockVec3i(10, 64, 10);
        for (int y = 64; y < 69; y++) {
            world.setBlock(new BlockVec3i(10, y, 10), "minecraft:spruce_log");
        }

        TreeFellResult res = engine.fellTree(world, root, false, "minecraft:diamond_axe", 1561);

        assertFalse(res.success());
        assertEquals("CABIN_PROTECTED", res.reason());
        assertEquals(1, res.felledLogs()); // Only root log broken

        // Logs above MUST remain intact
        assertEquals("minecraft:spruce_log", world.getBlockId(new BlockVec3i(10, 65, 10)));
        assertEquals("minecraft:spruce_log", world.getBlockId(new BlockVec3i(10, 66, 10)));
    }

    @Test
    void testSneakModeInversionSingleLog() {
        BlockVec3i root = buildNaturalTree(64, 5);
        TreeFellResult res = engine.fellTree(world, root, true, "minecraft:iron_axe", 250);

        assertTrue(res.success());
        assertEquals("SNEAK_SINGLE_LOG", res.reason());
        assertEquals(1, res.felledLogs());

        // Log above remains intact
        assertEquals("minecraft:oak_log", world.getBlockId(new BlockVec3i(0, 65, 0)));
    }

    @Test
    void testToolDurabilityTruncation() {
        BlockVec3i root = buildNaturalTree(64, 5);
        // Tool with only 2 durability remaining
        TreeFellResult res = engine.fellTree(world, root, false, "minecraft:golden_axe", 2);

        assertTrue(res.success());
        assertEquals(2, res.felledLogs());
        assertEquals(2, res.damageDealt());

        // 3rd log onwards remains intact
        assertEquals("minecraft:oak_log", world.getBlockId(new BlockVec3i(0, 66, 0)));
    }

    @Test
    void testMaxLogsLimit() {
        BlockVec3i root = new BlockVec3i(0, 64, 0);
        // Build large cluster of 300 logs
        int count = 0;
        for (int y = 64; y < 84; y++) {
            for (int x = 0; x < 4; x++) {
                for (int z = 0; z < 4; z++) {
                    world.setBlock(new BlockVec3i(x, y, z), "minecraft:spruce_log");
                    count++;
                }
            }
        }
        // Place canopy leaves
        for (int y = 78; y < 84; y++) {
            world.setBlock(new BlockVec3i(4, y, 0), "minecraft:spruce_leaves");
            world.setBlock(new BlockVec3i(0, y, 4), "minecraft:spruce_leaves");
        }

        TreeFellResult res = engine.fellTree(world, root, false, "minecraft:netherite_axe", 5000);

        assertTrue(res.success());
        assertEquals(256, res.felledLogs()); // Strictly bounded by 256
        assertEquals(256, res.damageDealt());
    }

    @Test
    void testRequireAxeRejection() {
        BlockVec3i root = buildNaturalTree(64, 5);
        TreeFellResult res = engine.fellTree(world, root, false, "minecraft:diamond_sword", 1561);

        assertFalse(res.success());
        assertEquals("NO_AXE", res.reason());
        assertEquals(1, res.felledLogs());
        // Remaining logs intact
        assertEquals("minecraft:oak_log", world.getBlockId(new BlockVec3i(0, 65, 0)));
    }
}
