package dev.natowb.natosatlas.core.renderer;

import dev.natowb.natosatlas.core.utils.NacColorMapper;
import dev.natowb.natosatlas.core.NacPlatformAPI;
import dev.natowb.natosatlas.core.models.NacChunk;
import dev.natowb.natosatlas.core.models.NacRegionData;
import dev.natowb.natosatlas.core.utils.NacConstants;

public class NacRegionRendererSmooth implements NacRegionRenderer {

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

                int[] chunkPixels = getChunkPixels(worldChunkX, worldChunkZ);
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

    private static boolean isWater(int id) {
        return id == 8 || id == 9;
    }

    private static int getColor(int x, int z, NacChunk chunk) {
        int height = chunk.heights[z * 16 + x];
        if (height <= 0) return 0xFF000000;

        int blockId = chunk.blockIds[z * 16 + x];
        int base = NacColorMapper.get(blockId);

        if (isWater(blockId))
            return applySmoothShade(base, smoothWaterShade(x, z, chunk));

        return applySmoothShade(base, smoothTerrainShade(x, z, chunk));
    }


    private static float smoothWaterShade(int x, int z, NacChunk chunk) {
        int depth = chunk.waterDepths[z * 16 + x];
        float shade = (float) Math.exp(-depth * 0.15);
        return Math.max(0.55f, shade);
    }


    private static float smoothTerrainShade(int x, int z, NacChunk chunk) {
        int idx = z * 16 + x;
        int h = chunk.heights[idx];

        int hN = chunk.heights[Math.max(0, z - 1) * 16 + x];
        int hS = chunk.heights[Math.min(15, z + 1) * 16 + x];
        int hW = chunk.heights[z * 16 + Math.max(0, x - 1)];
        int hE = chunk.heights[z * 16 + Math.min(15, x + 1)];

        float dx = (hE - hW) * 0.5f;
        float dz = (hS - hN) * 0.5f;

        float slope = (float) Math.sqrt(dx * dx + dz * dz);


        return 1.0f - Math.min(0.25f, slope * 0.05f);
    }

    private static int applySmoothShade(int base, float brightness) {
        int r = (int) ((base >> 16 & 255) * brightness);
        int g = (int) ((base >> 8 & 255) * brightness);
        int b = (int) ((base & 255) * brightness);

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public static int[] getChunkPixels(int chunkX, int chunkZ) {
        NacChunk chunk = NacPlatformAPI.get().chunkProvider.buildChunk(chunkX, chunkZ);
        int[] pixels = new int[256];

        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                pixels[z * 16 + x] = getColor(x, z, chunk);
            }
        }
        return pixels;
    }
}
