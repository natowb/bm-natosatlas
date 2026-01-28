package dev.natowb.natosatlas.core.chunk;

import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NARegionPixelData;

import static dev.natowb.natosatlas.core.NAConstants.BLOCKS_PER_CANVAS_REGION;
import static dev.natowb.natosatlas.core.NAConstants.BLOCKS_PER_MINECRAFT_CHUNK;

public class ChunkCaveRenderer implements ChunkRenderer {

    private final ChunkColorEngine colorEngine = new ChunkColorEngine();

    @Override
    public void applyChunkToRegion(NARegionPixelData region, NACoord chunkCoord, boolean useBlockLight, boolean fromDisk) {
        NAChunk chunk = fromDisk ? ChunkBuilder.buildCaveChunkFromDisk(chunkCoord) : ChunkBuilder.buildCaveChunk(chunkCoord);
        if (chunk == null) return;

        int[] pixels = region.getPixels();

        int offsetX = (chunkCoord.x & 31) * BLOCKS_PER_MINECRAFT_CHUNK;
        int offsetZ = (chunkCoord.z & 31) * BLOCKS_PER_MINECRAFT_CHUNK;

        for (int z = 0; z < 16; z++) {
            int row = (offsetZ + z) * BLOCKS_PER_CANVAS_REGION;

            for (int x = 0; x < 16; x++) {
                int color = colorEngine.getColor(x, z, chunk, true);
                pixels[row + offsetX + x] = color;
            }
        }
    }
}


