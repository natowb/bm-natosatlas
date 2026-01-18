package dev.natowb.natosatlas.core.map;

import static dev.natowb.natosatlas.core.utils.Constants.BLOCKS_PER_MINECRAFT_CHUNK;

public class MapChunk {
    public final int[] blockIds;
    public final int[] heights;
    public final int[] waterDepths;
    public final int[] blockLight;

    public MapChunk() {
        this.blockIds = new int[BLOCKS_PER_MINECRAFT_CHUNK * BLOCKS_PER_MINECRAFT_CHUNK];
        this.heights = new int[BLOCKS_PER_MINECRAFT_CHUNK * BLOCKS_PER_MINECRAFT_CHUNK];
        this.waterDepths = new int[BLOCKS_PER_MINECRAFT_CHUNK * BLOCKS_PER_MINECRAFT_CHUNK];
        this.blockLight = new int[BLOCKS_PER_MINECRAFT_CHUNK * BLOCKS_PER_MINECRAFT_CHUNK];
    }

    private int index(int x, int z) {
        return z * BLOCKS_PER_MINECRAFT_CHUNK + x;
    }

    public void set(int x, int z, int blockId, int height, int waterDepth, int blockLight) {
        int i = index(x, z);
        blockIds[i] = blockId;
        heights[i] = height;
        waterDepths[i] = waterDepth;
        this.blockLight[i] = blockLight;
    }

}
