package dev.natowb.natosatlas.core.chunk;

import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NARegionPixelData;

import static dev.natowb.natosatlas.core.NAConstants.BLOCKS_PER_CANVAS_REGION;
import static dev.natowb.natosatlas.core.NAConstants.BLOCKS_PER_MINECRAFT_CHUNK;

public class ChunkRenderer {

    private static final ChunkColorEngine colorEngine = new ChunkColorEngine();

    public static void render(NARegionPixelData region, NACoord chunkCoord, NAChunk chunk, boolean useBlockLight) {
        if (chunk == null) return;

        int[] pixels = region.getPixels();

        int regionBlockOffsetX = (chunkCoord.x & 31) * BLOCKS_PER_MINECRAFT_CHUNK;
        int regionBlockOffsetZ = (chunkCoord.z & 31) * BLOCKS_PER_MINECRAFT_CHUNK;

        for (int localZ = 0; localZ < BLOCKS_PER_MINECRAFT_CHUNK; localZ++) {
            int pixelRow = (regionBlockOffsetZ + localZ) * BLOCKS_PER_CANVAS_REGION;

            for (int localX = 0; localX < BLOCKS_PER_MINECRAFT_CHUNK; localX++) {
                int color = colorEngine.getColor(localX, localZ, chunk, useBlockLight);
                pixels[pixelRow + regionBlockOffsetX + localX] = color;
            }
        }
    }
}