package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.map.MapRegion;

import static dev.natowb.natosatlas.core.utils.Constants.BLOCKS_PER_CANVAS_REGION;
import static dev.natowb.natosatlas.core.utils.Constants.BLOCKS_PER_MINECRAFT_CHUNK;

public class MapChunkRendererCave implements MapChunkRenderer {

    private final ColorEngine colorEngine = new ColorEngine();

    @Override
    public void applyChunkToRegion(MapRegion region, NACoord chunkCoord, boolean useBlockLight) {
        int playerY = (int) NatosAtlas.get().getCurrentWorld().getPlayer().y;
        NAChunk chunk = ChunkBuilder.buildCaveChunk(NatosAtlas.get().getCurrentWorld(), chunkCoord, playerY);

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


