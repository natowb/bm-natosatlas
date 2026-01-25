package dev.natowb.natosatlas.core.chunk;

import dev.natowb.natosatlas.core.access.BlockAccess;
import dev.natowb.natosatlas.core.access.WorldAccess;

public abstract class ChunkWrapper {

    protected final Object chunk;

    public ChunkWrapper(Object chunk) {
        this.chunk = chunk;
    }

    public int getTopSolidBlockY(int x, int z) {

        int y = WorldAccess.get().getWorldHeight() - 1;
        x &= 15;
        int z0 = z & 15;

        BlockAccess blocks = BlockAccess.get();

        for (; y > 0; --y) {
            int blockId = getBlockId(x, y, z0);
            if (blocks.isBlock(blockId, BlockAccess.BlockIdentifier.GLASS)) continue;
            if (blocks.blocksMovement(blockId) || blocks.isFluid(blockId)) return y + 1;
        }

        return -1;
    }

    public int computeFluidDepth(int x, int y, int z) {
        if (y < 0) return 0;
        int depth = 0;
        while (y > 0) {
            int id = getBlockId(x, y, z);
            if (!BlockAccess.get().isFluid(id)) break;
            depth++;
            y--;
        }
        return depth;
    }

    public abstract long getLastSaveTime();

    public abstract boolean isDirty();

    public abstract int getBlockId(int x, int y, int z);

    public abstract int getBlockMeta(int x, int y, int z);

    public abstract int getBlockLight(int x, int y, int z);

    public abstract int getSkyLight(int x, int y, int z);
}
