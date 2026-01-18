package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.data.NABiome;
import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.utils.ColorMapperUtil;

import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.*;
import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.BLOCK_LAVA_MOVING_ID;
import static dev.natowb.natosatlas.core.utils.Constants.BLOCKS_PER_CANVAS_REGION;
import static dev.natowb.natosatlas.core.utils.Constants.BLOCKS_PER_MINECRAFT_CHUNK;

public class MapChunkRendererSurface implements MapChunkRenderer {

    @Override
    public void applyChunkToRegion(MapRegion region, NACoord chunkCoord, NAChunk chunk, boolean useBlockLight) {
        int[] pixels = region.getPixels();
        int regionBlockOffsetX = (chunkCoord.x & 31) * BLOCKS_PER_MINECRAFT_CHUNK;
        int regionBlockOffsetZ = (chunkCoord.z & 31) * BLOCKS_PER_MINECRAFT_CHUNK;
        for (int localBlockZ = 0; localBlockZ < BLOCKS_PER_MINECRAFT_CHUNK; localBlockZ++) {
            int pixelRow = (regionBlockOffsetZ + localBlockZ) * BLOCKS_PER_CANVAS_REGION;
            for (int localBlockX = 0; localBlockX < BLOCKS_PER_MINECRAFT_CHUNK; localBlockX++) {
                int color = getBlockColor(localBlockX, localBlockZ, chunk);
                if (useBlockLight) {
                    int localIndex = localBlockZ * 16 + localBlockX;
                    color = applyBlockLight(color, chunk.blockLight[localIndex]);
                }
                pixels[pixelRow + regionBlockOffsetX + localBlockX] = color;
            }
        }
    }

    private int getBlockColor(int localBlockX, int localBlockZ, NAChunk chunk) {
        int localIndex = NAChunk.index(localBlockX, localBlockZ);

        int height = chunk.heights[localIndex];
        if (height <= 0) return 0xFF000000;

        int blockId = chunk.blockIds[localIndex];
        int blockMeta = chunk.meta[localIndex];
        int baseColor = ColorMapperUtil.get(blockId, blockMeta);
        NABiome biome = chunk.biome[localIndex];

        if (blockId == BLOCK_GRASS_ID) {
            baseColor = mixColors(baseColor, biome.grassColor, 0.1f);
        }

        if (blockId == BLOCK_LEAVES_ID) {
            baseColor = mixColors(baseColor, biome.foliageColor, 0.4f);
        }

        if(chunk.slimeChunk) {
            baseColor = mixColors(baseColor, 0xFF0000, 0.5F);
        }

        if (isFluid(blockId))
            return waterColor(localBlockX, localBlockZ, chunk, baseColor);

        int prevLocalZ = Math.max(0, localBlockZ - 1);
        int heightDiff = height - chunk.heights[prevLocalZ * 16 + localBlockX];
        int shade = heightDiff > 0 ? 2 : heightDiff < 0 ? 0 : 1;

        return applyShade(baseColor, shade);
    }


    private final int[] BRIGHTNESS = {180, 220, 255, 135};

    public boolean isFluid(int blockId) {
        return blockId == BLOCK_WATER_STILL_ID ||
                blockId == BLOCK_WATER_MOVING_ID ||
                blockId == BLOCK_LAVA_STILL_ID ||
                blockId == BLOCK_LAVA_MOVING_ID;
    }

    public int waterColor(int localBlockX, int localBlockZ, NAChunk chunk, int baseColor) {
        int localIndex = localBlockZ * 16 + localBlockX;

        int waterDepth = chunk.waterDepths[localIndex];

        double noise = ((localBlockX + localBlockZ) & 1) * 0.2;

        double depthFactor = waterDepth * 0.1 + noise;

        int shadeIndex =
                (depthFactor < 0.5) ? 2 :
                        (depthFactor > 0.9) ? 0 :
                                1;

        return applyShade(baseColor, shadeIndex);
    }

    public int applyShade(int baseColor, int shadeIndex) {
        int brightnessFactor = BRIGHTNESS[shadeIndex];

        int baseRed = (baseColor >> 16) & 255;
        int baseGreen = (baseColor >> 8) & 255;
        int baseBlue = baseColor & 255;

        int r = baseRed * brightnessFactor / 255;
        int g = baseGreen * brightnessFactor / 255;
        int b = baseBlue * brightnessFactor / 255;

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public int applyBlockLight(int baseColor, int blockLightLevel) {
        float minBrightness = 0.20f;
        float lightFactor = Math.max(minBrightness, blockLightLevel / 15f);

        int baseRed = (baseColor >> 16) & 255;
        int baseGreen = (baseColor >> 8) & 255;
        int baseBlue = baseColor & 255;

        int r = (int) (baseRed * lightFactor);
        int g = (int) (baseGreen * lightFactor);
        int b = (int) (baseBlue * lightFactor);

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private int mixColors(int baseColor, int tintColor, float tintAmount) {
        float baseAmount = 1f - tintAmount;

        int br = (baseColor >> 16) & 255;
        int bg = (baseColor >> 8) & 255;
        int bb = baseColor & 255;

        int tr = (tintColor >> 16) & 255;
        int tg = (tintColor >> 8) & 255;
        int tb = tintColor & 255;

        int r = (int) (br * baseAmount + tr * tintAmount);
        int g = (int) (bg * baseAmount + tg * tintAmount);
        int b = (int) (bb * baseAmount + tb * tintAmount);

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}

