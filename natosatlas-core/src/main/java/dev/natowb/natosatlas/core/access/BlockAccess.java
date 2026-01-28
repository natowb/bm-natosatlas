package dev.natowb.natosatlas.core.access;

import dev.natowb.natosatlas.core.io.LogUtil;

import java.util.HashMap;

public abstract class BlockAccess {
    public enum BlockIdentifier {
        GRASS,
        GLASS,
        SNOW
    }

    private final HashMap<BlockIdentifier, Integer> blockIdLookup = new HashMap<>();

    protected void registerBlockIdentifier(BlockIdentifier block, int blockId) {
        blockIdLookup.put(block, blockId);
    }

    public boolean isBlock(int blockId, BlockIdentifier block) {
        if (!blockIdLookup.containsKey(block)) {
            LogUtil.error("BlockAccess: blockIdLookup not registered for {}", block);
        }
        int lookupId = blockIdLookup.getOrDefault(block, -1);
        return blockId == lookupId;
    }

    public abstract int getColor(int blockId, int blockMeta);

    public abstract boolean isFluid(int blockId);

    public abstract boolean blocksMovement(int blockId);
}
