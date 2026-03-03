package dev.natowb.natosatlas.core.chunk;

import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.client.platform.BlockAccess;

public final class ChunkColorEngine {

    private static final int[] BRIGHTNESS = {180, 220, 255, 135};

    public int getColor(int localX, int localZ, NAChunk chunk, boolean useBlockLight) {
        int index = NAChunk.index(localX, localZ);

        int height = chunk.heights[index];
        if (height < 0) {
            return 0xFF000000;
        }

        int blockId = chunk.blockIds[index];
        int meta    = chunk.meta[index];


        int baseColor = BlockAccess.get().getColor(blockId, meta);

        int shadeIndex;

        if (BlockAccess.get().isFluid(blockId)) {
            shadeIndex = computeWaterBrightness(localX, localZ, chunk);
        } else {
            shadeIndex = computeHeightBrightness(localX, localZ, chunk);
        }

        return applyShade(baseColor, shadeIndex);
    }


    private int computeWaterBrightness(int localX, int localZ, NAChunk chunk) {
        int index = NAChunk.index(localX, localZ);
        int waterDepth = chunk.waterDepths[index];

        double noise = ((localX + localZ) & 1) * 0.2;
        double d3 = waterDepth * 0.1 + noise;

        int brightness = 1;
        if (d3 < 0.5) {
            brightness = 2;
        } else if (d3 > 0.9) {
            brightness = 0;
        }

        return brightness;
    }

    private int computeHeightBrightness(int localX, int localZ, NAChunk chunk) {
        int index = NAChunk.index(localX, localZ);
        int height = chunk.heights[index];

        int prevZ = Math.max(0, localZ - 1);
        int prevIndex = NAChunk.index(localX, prevZ);
        int prevHeight = chunk.heights[prevIndex];

        double heightDiff = (height - prevHeight) * (4.0 / 5.0)
                + (((localX + localZ) & 1) - 0.5) * 0.4;

        int brightness = 1;
        if (heightDiff > 0.6) {
            brightness = 2;
        } else if (heightDiff < -0.6) {
            brightness = 0;
        }

        return brightness;
    }

    private int applyShade(int baseColor, int shadeIndex) {
        int shade = BRIGHTNESS[shadeIndex & 3];

        int r = ((baseColor >> 16) & 255) * shade / 255;
        int g = ((baseColor >> 8) & 255) * shade / 255;
        int b = (baseColor & 255) * shade / 255;

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
