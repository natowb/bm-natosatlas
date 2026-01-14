package dev.natowb.natosatlas.core.renderer;

import dev.natowb.natosatlas.core.glue.NacPlatformAPI;
import dev.natowb.natosatlas.core.models.NacChunk;
import dev.natowb.natosatlas.core.models.NacRegionData;
import dev.natowb.natosatlas.core.utils.NacConstants;

public class NacRegionRendererHeightmap implements NacRegionRenderer {

    private static final int CHUNK_SIZE = NacConstants.BLOCKS_PER_MINECRAFT_CHUNK;
    private static final int REGION_SIZE = NacConstants.BLOCKS_PER_CANVAS_REGION;
    private static final int CHUNKS_PER_REGION = NacConstants.CHUNKS_PER_MINECRAFT_REGION;

    @Override
    public void buildPixels(NacRegionData region, int[] outPixels) {
        for (int cx = 0; cx < CHUNKS_PER_REGION; cx++) {
            for (int cz = 0; cz < CHUNKS_PER_REGION; cz++) {

                if (region.getChunk(cx, cz) == null) continue;

                int worldChunkX = (region.getRegionX() << 5) + cx;
                int worldChunkZ = (region.getRegionZ() << 5) + cz;

                int[] chunkPixels = getChunkHeightPixels(worldChunkX, worldChunkZ);
                blitChunk(outPixels, chunkPixels, cx, cz);
            }
        }
    }

    private void blitChunk(int[] out, int[] chunk, int cx, int cz) {
        int offsetX = cx * CHUNK_SIZE;
        int offsetZ = cz * CHUNK_SIZE;

        for (int z = 0; z < CHUNK_SIZE; z++) {
            int destRow = (offsetZ + z) * REGION_SIZE;
            int srcRow = z * CHUNK_SIZE;
            System.arraycopy(chunk, srcRow, out, destRow + offsetX, CHUNK_SIZE);
        }
    }

    private static int[] getChunkHeightPixels(int chunkX, int chunkZ) {
        NacChunk chunk = NacPlatformAPI.get().chunkProvider.buildChunk(chunkX, chunkZ);
        int[] pixels = new int[256];

        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                int h = chunk.heights[z * 16 + x];
                int shade = Math.min(255, Math.max(0, h));
                pixels[z * 16 + x] = 0xFF000000 | (shade << 16) | (shade << 8) | shade;
            }
        }
        return pixels;
    }
}
