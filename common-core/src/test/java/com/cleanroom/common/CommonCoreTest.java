package com.cleanroom.common;

import com.cleanroom.common.api.*;
import com.cleanroom.common.config.JsonConfigProvider;
import com.cleanroom.common.event.BlockBreakEvent;
import com.cleanroom.common.event.CommonEventBus;
import com.cleanroom.common.math.*;
import com.cleanroom.common.model.BlockBox;
import com.cleanroom.common.model.ContainerSlot;
import com.cleanroom.common.telemetry.CleanRoomTelemetry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class CommonCoreTest {

    @BeforeEach
    void setup() {
        CommonEventBus.getInstance().clear();
        CleanRoomTelemetry.clear();
    }

    @Test
    void testBlockVec3iMath() {
        BlockVec3i a = new BlockVec3i(10, 64, -20);
        BlockVec3i b = a.offset(1, -2, 3);
        assertEquals(11, b.x());
        assertEquals(62, b.y());
        assertEquals(-17, b.z());

        assertEquals(new BlockVec3i(10, 65, -20), a.above());
        assertEquals(new BlockVec3i(10, 63, -20), a.below());
        assertEquals(new BlockVec3i(10, 64, -21), a.north());
        assertEquals(new BlockVec3i(10, 64, -19), a.south());
        assertEquals(new BlockVec3i(9, 64, -20), a.west());
        assertEquals(new BlockVec3i(11, 64, -20), a.east());

        assertEquals(6, a.getNeighbors6().size());
        assertEquals(26, a.getNeighbors26().size());

        BlockVec3i target = new BlockVec3i(10, 64, -15);
        assertEquals(25.0, a.distSq(target), 1E-5);
        assertEquals(5.0, a.dist(target), 1E-5);
        assertEquals(5, a.manhattanDist(target));
    }

    @Test
    void testVec3dMath() {
        Vec3d v1 = new Vec3d(1.0, 2.0, 3.0);
        Vec3d v2 = new Vec3d(4.0, -1.0, 2.0);

        Vec3d sum = v1.add(v2);
        assertEquals(5.0, sum.x(), 1E-5);
        assertEquals(1.0, sum.y(), 1E-5);
        assertEquals(5.0, sum.z(), 1E-5);

        assertEquals(8.0, v1.dot(v2), 1E-5); // 1*4 + 2*(-1) + 3*2 = 4 - 2 + 6 = 8
        assertEquals(14.0, v1.lengthSq(), 1E-5); // 1 + 4 + 9 = 14
        assertEquals(Math.sqrt(14.0), v1.length(), 1E-5);

        Vec3d norm = v1.normalize();
        assertEquals(1.0, norm.length(), 1E-5);
    }

    @Test
    void testSpatialBfsConnectedSearch() {
        Set<BlockVec3i> simulatedLogCluster = new HashSet<>();
        // Tree trunk: (0, 64, 0) up to (0, 68, 0)
        for (int y = 64; y <= 68; y++) {
            simulatedLogCluster.add(new BlockVec3i(0, y, 0));
        }
        // Small branch at (1, 67, 0) and (2, 67, 0)
        simulatedLogCluster.add(new BlockVec3i(1, 67, 0));
        simulatedLogCluster.add(new BlockVec3i(2, 67, 0));

        // Isolated log far away: (10, 64, 10)
        simulatedLogCluster.add(new BlockVec3i(10, 64, 10));

        BlockVec3i root = new BlockVec3i(0, 64, 0);
        List<BlockVec3i> discovered = SpatialBfs.findConnected(
                root,
                simulatedLogCluster::contains,
                100,
                16.0
        );

        // Discovered should contain 7 trunk and branch logs, but NOT the isolated log
        assertEquals(7, discovered.size());
        assertTrue(discovered.contains(root));
        assertTrue(discovered.contains(new BlockVec3i(2, 67, 0)));
        assertFalse(discovered.contains(new BlockVec3i(10, 64, 10)));
    }

    @Test
    void testRaycastIntersection() {
        Vec3d rayOrigin = new Vec3d(0.0, 1.5, 0.0);
        Vec3d rayDir = new Vec3d(0.0, 0.0, 1.0); // Facing straight South

        Vec3d boxMin = new Vec3d(-0.5, 0.0, 5.0);
        Vec3d boxMax = new Vec3d(0.5, 2.0, 6.0);

        RaycastMath.RayResult result = RaycastMath.intersectAABB(rayOrigin, rayDir, boxMin, boxMax);
        assertTrue(result.hit());
        assertEquals(5.0, result.distance(), 1E-5);
        assertEquals(new Vec3d(0.0, 1.5, 5.0), result.hitPoint());

        // Ray pointing opposite direction should miss
        Vec3d oppDir = new Vec3d(0.0, 0.0, -1.0);
        RaycastMath.RayResult oppResult = RaycastMath.intersectAABB(rayOrigin, oppDir, boxMin, boxMax);
        assertFalse(oppResult.hit());
    }

    @Test
    void testBlockBoxCollision() {
        BlockBox b1 = BlockBox.fromPoints(new Vec3d(0, 0, 0), new Vec3d(2, 2, 2));
        BlockBox b2 = BlockBox.fromPoints(new Vec3d(1, 1, 1), new Vec3d(3, 3, 3));
        BlockBox b3 = BlockBox.fromPoints(new Vec3d(5, 5, 5), new Vec3d(7, 7, 7));

        assertTrue(b1.intersects(b2));
        assertFalse(b1.intersects(b3));

        assertTrue(b1.contains(new Vec3d(1.5, 1.5, 1.5)));
        assertFalse(b1.contains(new Vec3d(2.5, 1.5, 1.5)));
    }

    public static class SampleConfig {
        public boolean enabled = true;
        public int maxRange = 64;
        public String mode = "AUTO";
    }

    @Test
    void testJsonConfigProvider(@TempDir Path tempDir) {
        Path cfgPath = tempDir.resolve("sample_mod.json");
        SampleConfig def = new SampleConfig();
        def.maxRange = 128;

        SampleConfig loaded = JsonConfigProvider.loadConfig(cfgPath, SampleConfig.class, def);
        assertEquals(128, loaded.maxRange);
        assertTrue(loaded.enabled);

        // Modify and save
        loaded.mode = "CUSTOM";
        boolean saved = JsonConfigProvider.saveConfig(cfgPath, loaded);
        assertTrue(saved);

        SampleConfig reloaded = JsonConfigProvider.loadConfig(cfgPath, SampleConfig.class, new SampleConfig());
        assertEquals("CUSTOM", reloaded.mode);
        assertEquals(128, reloaded.maxRange);
    }

    @Test
    void testCommonEventBus() {
        CommonEventBus bus = CommonEventBus.getInstance();
        AtomicInteger breakCount = new AtomicInteger(0);

        bus.register(BlockBreakEvent.class, event -> {
            breakCount.incrementAndGet();
            if (event.getBlockId().equals("minecraft:bedrock")) {
                event.setCanceled(true);
            }
        });

        BlockBreakEvent e1 = new BlockBreakEvent(new BlockVec3i(0, 60, 0), "minecraft:oak_log", "player1", "minecraft:diamond_axe", false);
        bus.post(e1);
        assertEquals(1, breakCount.get());
        assertFalse(e1.isCanceled());

        BlockBreakEvent e2 = new BlockBreakEvent(new BlockVec3i(0, -64, 0), "minecraft:bedrock", "player1", "minecraft:diamond_axe", false);
        bus.post(e2);
        assertEquals(2, breakCount.get());
        assertTrue(e2.isCanceled());
    }

    @Test
    void testPlatformManagerFallback() {
        PlatformAdapter adapter = PlatformManager.getPlatform();
        assertNotNull(adapter);
        assertEquals(PlatformType.STANDALONE, adapter.getPlatformType());
        assertEquals(EnvironmentSide.SERVER, adapter.getEnvironmentSide());
        assertFalse(adapter.isModLoaded("some_mod"));
    }

    @Test
    void testCleanRoomTelemetry() {
        CleanRoomTelemetry.record("EVENT", "Test message 1");
        CleanRoomTelemetry.record("AUDIT", "Test message 2");

        List<CleanRoomTelemetry.TelemetryRecord> snapshot = CleanRoomTelemetry.getSnapshot();
        assertEquals(2, snapshot.size());
        assertEquals("EVENT", snapshot.get(0).category());
        assertEquals("AUDIT", snapshot.get(1).category());
    }

    @Test
    void testContainerSlotModel() {
        ContainerSlot emptySlot = ContainerSlot.empty(0);
        assertTrue(emptySlot.isEmpty());

        ContainerSlot itemSlot = new ContainerSlot(1, "minecraft:diamond", 10, 64, Collections.emptyMap());
        assertFalse(itemSlot.isEmpty());
        assertEquals(10, itemSlot.count());
    }
}
