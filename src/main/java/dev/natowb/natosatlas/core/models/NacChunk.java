package dev.natowb.natosatlas.core.models;

public class NacChunk {
    public final int[] blockIds;
    public final int[] heights;
    public final int[] waterDepths;

    public NacChunk() {
        this.blockIds = new int[16 * 16];
        this.heights = new int[16 * 16];
        this.waterDepths = new int[16 * 16];
    }

    private int index(int x, int z) {
        return z * 16 + x;
    }

    public void set(int x, int z, int blockId, int height, int waterDepth) {
        int i = index(x, z);
        blockIds[i] = blockId;
        heights[i] = height;
        waterDepths[i] = waterDepth;
    }
}
