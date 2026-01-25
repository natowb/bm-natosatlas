package dev.natowb.natosatlas.core.chunk;

import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.map.MapRegion;

import static dev.natowb.natosatlas.core.NatoAtlasConstants.BLOCKS_PER_CANVAS_REGION;
import static dev.natowb.natosatlas.core.NatoAtlasConstants.BLOCKS_PER_MINECRAFT_CHUNK;

public class ChunkSurfaceRenderer implements ChunkRenderer {

    private final ChunkColorEngine colorEngine = new ChunkColorEngine();

    @Override
    public void applyChunkToRegion(MapRegion region, NACoord chunkCoord, boolean useBlockLight, boolean fromDisk) {
        NAChunk chunk = fromDisk ? ChunkBuilder.buildChunkSurfaceFromDisk(chunkCoord) : ChunkBuilder.buildChunkSurface(chunkCoord);
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
