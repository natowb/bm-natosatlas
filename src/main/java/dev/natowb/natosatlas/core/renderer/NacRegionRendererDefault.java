package dev.natowb.natosatlas.core.renderer;

import dev.natowb.natosatlas.core.glue.NacPlatformAPI;
import dev.natowb.natosatlas.core.models.NacChunk;
import dev.natowb.natosatlas.core.models.NacRegionData;
import dev.natowb.natosatlas.core.utils.NacConstants;
import dev.natowb.natosatlas.core.colors.NacColorMapper;

public class NacRegionRendererDefault implements NacRegionRenderer {

    private static final int CHUNK_SIZE = NacConstants.BLOCKS_PER_MINECRAFT_CHUNK;
    private static final int REGION_SIZE = NacConstants.BLOCKS_PER_CANVAS_REGION;
    private static final int CHUNKS_PER_REGION = NacConstants.CHUNKS_PER_MINECRAFT_REGION;

    private static final int[] MC_BRIGHTNESS = {180, 220, 255, 135};

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

        if (isWater(blockId)) return waterColor(x, z, chunk, base);

        int shade = heightShade(x, z, chunk);
        return applyShade(base, shade);
    }

    private static int waterColor(int x, int z, NacChunk chunk, int base) {
        int depth = chunk.waterDepths[z * 16 + x];
        double noise = ((x + z) & 1) * 0.2;
        double d = depth * 0.1 + noise;

        int shade = (d < 0.5) ? 2 : (d > 0.9) ? 0 : 1;
        return applyShade(base, shade);
    }

    private static int heightShade(int x, int z, NacChunk chunk) {
        int idx = z * 16 + x;
        int y = chunk.heights[idx];

        int prevZ = Math.max(0, z - 1);
        int prevY = chunk.heights[prevZ * 16 + x];

        int dy = y - prevY;
        return dy > 0 ? 2 : dy < 0 ? 0 : 1;
    }

    private static int applyShade(int base, int shadeIndex) {
        int b = MC_BRIGHTNESS[shadeIndex];
        int r = (base >> 16 & 255) * b / 255;
        int g = (base >> 8 & 255) * b / 255;
        int bl = (base & 255) * b / 255;
        return 0xFF000000 | (r << 16) | (g << 8) | bl;
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
