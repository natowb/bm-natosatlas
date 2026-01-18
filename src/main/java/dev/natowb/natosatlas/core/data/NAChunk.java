package dev.natowb.natosatlas.core.data;

import static dev.natowb.natosatlas.core.utils.Constants.BLOCKS_PER_MINECRAFT_CHUNK;

public class NAChunk {
    public final int[] blockIds;
    public final int[] heights;
    public final int[] waterDepths;
    public final int[] meta;
    public final int[] blockLight;
    public final NABiome[] biome;
    public boolean slimeChunk = false;

    public NAChunk() {
        this.blockIds = new int[BLOCKS_PER_MINECRAFT_CHUNK * BLOCKS_PER_MINECRAFT_CHUNK];
        this.heights = new int[BLOCKS_PER_MINECRAFT_CHUNK * BLOCKS_PER_MINECRAFT_CHUNK];
        this.waterDepths = new int[BLOCKS_PER_MINECRAFT_CHUNK * BLOCKS_PER_MINECRAFT_CHUNK];
        this.blockLight = new int[BLOCKS_PER_MINECRAFT_CHUNK * BLOCKS_PER_MINECRAFT_CHUNK];
        this.meta = new int[BLOCKS_PER_MINECRAFT_CHUNK * BLOCKS_PER_MINECRAFT_CHUNK];
        this.biome = new NABiome[BLOCKS_PER_MINECRAFT_CHUNK * BLOCKS_PER_MINECRAFT_CHUNK];
    }

    public static int index(int x, int z) {
        return z * BLOCKS_PER_MINECRAFT_CHUNK + x;
    }

    public void set(int x, int z, int height, int blockId, int depth, int blockLight, int meta, NABiome biome, boolean slimeChunk) {
        int i = index(x, z);
        this.heights[i] = height;
        this.blockIds[i] = blockId;
        this.waterDepths[i] = depth;
        this.blockLight[i] = blockLight;
        this.meta[i] = meta;
        this.biome[i] = biome;
        this.slimeChunk = slimeChunk;
    }

}
