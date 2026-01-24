package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.data.NABiome;
import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.wrapper.BlockAccess;


public final class ColorEngine {

    private static final int[] BRIGHTNESS = {180, 220, 255, 135};

    public int getColor(int localX, int localZ, NAChunk chunk, boolean useBlockLight) {
        int index = NAChunk.index(localX, localZ);

        int height = chunk.heights[index];
        if (height < 0) {
            return 0xFF000000;
        }

        int blockId = chunk.blockIds[index];
        int meta = chunk.meta[index];
        NABiome biome = chunk.biome[index];

        int baseColor = BlockAccess.getInstance().getColor(blockId, meta);

        baseColor = applyBiomeTint(blockId, baseColor, biome);

        if (BlockAccess.getInstance().isFluid(blockId)) {
            baseColor = applyWaterTint(localX, localZ, chunk, baseColor);
        }

        baseColor = applyHeightShading(localX, localZ, chunk, baseColor);

        if (useBlockLight) {
            int blockLight = chunk.blockLight[index];
            baseColor = applyBlockLight(baseColor, blockLight);
        }

        return baseColor;
    }

    private int applyBiomeTint(int blockId, int baseColor, NABiome biome) {
        if (BlockAccess.getInstance().isBlock(blockId, BlockAccess.BlockIdentifier.GRASS)) {
            return mixColors(baseColor, biome.grassColor, 0.1f);
        }
        return baseColor;
    }

    private int applyWaterTint(int localX, int localZ, NAChunk chunk, int baseColor) {
        int index = NAChunk.index(localX, localZ);
        int waterDepth = chunk.waterDepths[index];

        double noise = ((localX + localZ) & 1) * 0.2;
        double depthFactor = waterDepth * 0.1 + noise;

        int shadeIndex =
                (depthFactor < 0.5) ? 2 :
                        (depthFactor > 0.9) ? 0 : 1;

        return applyShade(baseColor, shadeIndex);
    }

    private int applyHeightShading(int localX, int localZ, NAChunk chunk, int baseColor) {
        int index = NAChunk.index(localX, localZ);
        int height = chunk.heights[index];

        int prevZ = Math.max(0, localZ - 1);
        int prevIndex = prevZ * 16 + localX;
        int prevHeight = chunk.heights[prevIndex];

        int heightDiff = height - prevHeight;
        int shadeIndex = heightDiff > 0 ? 2 : heightDiff < 0 ? 0 : 1;

        return applyShade(baseColor, shadeIndex);
    }

    private int applyShade(int baseColor, int shadeIndex) {
        int brightnessFactor = BRIGHTNESS[shadeIndex];

        int r = ((baseColor >> 16) & 255) * brightnessFactor / 255;
        int g = ((baseColor >> 8) & 255) * brightnessFactor / 255;
        int b = (baseColor & 255) * brightnessFactor / 255;

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private int applyBlockLight(int baseColor, int blockLightLevel) {
        float minBrightness = 0.20f;
        float lightFactor = Math.max(minBrightness, blockLightLevel / 15f);

        int r = (int) (((baseColor >> 16) & 255) * lightFactor);
        int g = (int) (((baseColor >> 8) & 255) * lightFactor);
        int b = (int) ((baseColor & 255) * lightFactor);

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
