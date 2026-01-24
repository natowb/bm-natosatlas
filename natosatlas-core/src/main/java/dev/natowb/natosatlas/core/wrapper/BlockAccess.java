package dev.natowb.natosatlas.core.wrapper;

public abstract class BlockAccess {

    private static BlockAccess instance;

    public static void setInstance(BlockAccess instance) {
        BlockAccess.instance = instance;
    }

    public static BlockAccess getInstance() {
        return instance;
    }

    public abstract int getColor(int blockId, int blockMeta);

    public abstract boolean isFluid(int blockId);

    public abstract boolean isGrass(int blockId);
}
