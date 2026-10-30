package com.cleanroom.treecapitator;

import com.cleanroom.common.math.BlockVec3i;
import java.util.List;

public interface TreeWorldView {
    String getBlockId(BlockVec3i pos);
    boolean isLog(BlockVec3i pos);
    boolean isLeaf(BlockVec3i pos);
    boolean setBlockToAir(BlockVec3i pos);
    List<String> getDrops(BlockVec3i pos);
    void spawnDrops(BlockVec3i pos, List<String> drops);
}
