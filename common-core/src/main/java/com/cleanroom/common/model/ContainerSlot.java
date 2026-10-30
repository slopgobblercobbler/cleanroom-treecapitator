package com.cleanroom.common.model;

import java.util.Collections;
import java.util.Map;

public record ContainerSlot(
        int slotIndex,
        String itemId,
        int count,
        int maxCount,
        Map<String, Object> tagData
) {
    public static ContainerSlot empty(int slotIndex) {
        return new ContainerSlot(slotIndex, "minecraft:air", 0, 64, Collections.emptyMap());
    }

    public boolean isEmpty() {
        return count <= 0 || "minecraft:air".equals(itemId);
    }
}
