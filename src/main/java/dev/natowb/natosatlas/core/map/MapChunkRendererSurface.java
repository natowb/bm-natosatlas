package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.utils.ColorMapperUtil;

import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.*;
import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.BLOCK_LAVA_MOVING_ID;
import static dev.natowb.natosatlas.core.utils.Constants.BLOCKS_PER_CANVAS_REGION;
import static dev.natowb.natosatlas.core.utils.Constants.BLOCKS_PER_MINECRAFT_CHUNK;

public class MapChunkRendererSurface implements MapChunkRenderer {

    @Override
    public void applyChunkToRegion(MapRegion tile, int chunkX, int chunkZ, MapChunk chunk, boolean useBlockLight) {
        int[] pixels = tile.getPixels();
        int ox = (chunkX & 31) * BLOCKS_PER_MINECRAFT_CHUNK;
        int oz = (chunkZ & 31) * BLOCKS_PER_MINECRAFT_CHUNK;

        for (int z = 0; z < BLOCKS_PER_MINECRAFT_CHUNK; z++) {
            int row = (oz + z) * BLOCKS_PER_CANVAS_REGION;
            for (int x = 0; x < BLOCKS_PER_MINECRAFT_CHUNK; x++) {
                int c = color(x, z, chunk);
                if (useBlockLight) {
                    int idx = z * 16 + x;
                    c = applyBlockLight(c, chunk.blockLight[idx]);
                }
                pixels[row + ox + x] = c;
            }
        }
    }

    private int color(int x, int z, MapChunk chunk) {
        int idx = z * 16 + x;
        int y = chunk.heights[idx];
        if (y <= 0) return 0xFF000000;

        int id = chunk.blockIds[idx];
        int base = ColorMapperUtil.get(id);

        if (isFluid(id))
            return waterColor(x, z, chunk, base);

        int prevZ = Math.max(0, z - 1);
        int dy = y - chunk.heights[prevZ * 16 + x];
        int shade = dy > 0 ? 2 : dy < 0 ? 0 : 1;
        return applyShade(base, shade);

    }


    private final int[] BRIGHTNESS = {180, 220, 255, 135};

    public boolean isFluid(int id) {
        return id == BLOCK_WATER_STILL_ID ||
                id == BLOCK_WATER_MOVING_ID ||
                id == BLOCK_LAVA_STILL_ID ||
                id == BLOCK_LAVA_MOVING_ID;
    }

    public int waterColor(int x, int z, MapChunk chunk, int base) {
        int depth = chunk.waterDepths[z * 16 + x];
        double noise = ((x + z) & 1) * 0.2;
        double d = depth * 0.1 + noise;
        int shade = (d < 0.5) ? 2 : (d > 0.9) ? 0 : 1;
        return applyShade(base, shade);
    }

    public int applyShade(int base, int shadeIndex) {
        int b = BRIGHTNESS[shadeIndex];
        int r = (base >> 16 & 255) * b / 255;
        int g = (base >> 8 & 255) * b / 255;
        int bl = (base & 255) * b / 255;
        return 0xFF000000 | (r << 16) | (g << 8) | bl;
    }

    public int applyBlockLight(int color, int blockLight) {
        float min = 0.20f;
        float f = Math.max(min, blockLight / 15f);

        int r = (int) (((color >> 16) & 255) * f);
        int g = (int) (((color >> 8) & 255) * f);
        int b = (int) ((color & 255) * f);

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}

